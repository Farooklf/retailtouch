package com.lfssolutions.retialtouch.sync

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.domain.ApiUtils.observeResponse
import com.lfssolutions.retialtouch.domain.ApiUtils.observeResponseNew
import com.lfssolutions.retialtouch.domain.PreferencesRepository
import com.lfssolutions.retialtouch.domain.RequestState
import com.lfssolutions.retialtouch.domain.model.ApiLoaderStateResponse
import com.lfssolutions.retialtouch.domain.model.basic.BasicApiRequest
import com.lfssolutions.retialtouch.domain.model.invoiceSaleTransactions.GetPosInvoiceResult
import com.lfssolutions.retialtouch.domain.model.invoiceSaleTransactions.POSInvoiceRequest
import com.lfssolutions.retialtouch.domain.model.location.Location
import com.lfssolutions.retialtouch.domain.model.menu.CategoryItem
import com.lfssolutions.retialtouch.domain.model.products.Stock
import com.lfssolutions.retialtouch.domain.model.promotions.GetPromotionsByPriceResult
import com.lfssolutions.retialtouch.domain.model.promotions.GetPromotionsByQtyResult
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionDetails
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionItem
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionRequest
import com.lfssolutions.retialtouch.domain.model.sync.SyncItem
import com.lfssolutions.retialtouch.domain.model.sync.UnSyncList
import com.lfssolutions.retialtouch.presentation.viewModels.BaseViewModel
import com.lfssolutions.retialtouch.utils.AppConstants.CATEGORY
import com.lfssolutions.retialtouch.utils.AppConstants.INVENTORY_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.INVOICE
import com.lfssolutions.retialtouch.utils.AppConstants.MEMBER
import com.lfssolutions.retialtouch.utils.AppConstants.MEMBER_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.MEMBER_GROUP
import com.lfssolutions.retialtouch.utils.AppConstants.MENU
import com.lfssolutions.retialtouch.utils.AppConstants.MENU_CATEGORY_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.MENU_PRODUCTS_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.NEXT_SALE_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.PAYMENT_TYPE
import com.lfssolutions.retialtouch.utils.AppConstants.PAYMENT_TYPE_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.PRODUCT
import com.lfssolutions.retialtouch.utils.AppConstants.PROMOTION
import com.lfssolutions.retialtouch.utils.AppConstants.PROMOTIONS_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.SYNC_CHANGES_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.SYNC_SALES_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.DateTimeUtils.getCurrentDateAndTimeInEpochMilliSeconds
import com.lfssolutions.retialtouch.utils.DateTimeUtils.getLastSyncDateTime
import com.lfssolutions.retialtouch.utils.serializers.db.parsePriceBreakPromotionAttributes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SyncViewModel : BaseViewModel() , KoinComponent {

    //private val networkRepository: NetworkRepository by inject()
    private val preferencesRepository: PreferencesRepository by inject()
    //private val dataBaseRepository: DataBaseRepository by inject()

    private val _syncDataState = MutableStateFlow(SyncDataState())
    val syncDataState: StateFlow<SyncDataState> = _syncDataState.asStateFlow()
    private val stockQtyMap = MutableStateFlow<Map<Int,Double?>>(emptyMap())
    private val categoryList = MutableStateFlow<List<CategoryItem>>(emptyList())

    private var timerJob: Job? = null  // To manage the periodic sync job

    private val syncFlags = mutableStateMapOf(
        MEMBER to false,
        MEMBER_GROUP to false,
        PRODUCT to false,
        CATEGORY to false,
        MENU to false,
        PROMOTION to false,
        PAYMENT_TYPE to false
    )

    init {
        // Read reSync time from storage and start the sync timer
        viewModelScope.launch {
            _syncDataState.update { it.copy(reSyncTime = getReSyncTimer()) }
            startPeriodicSync()
        }
    }

    suspend fun reSyncAfterLogin(locationChange : Boolean = false)  {
        val pCount = dataBaseRepository.getAllPendingSaleRecordsCount().first()
        println("pCount :$pCount")
        if (pCount <= 0 || locationChange) {
            println("start sync everything")
            syncEveryThing()
        } else {
            println("start re sync items")
            reSyncItems()
        }
    }



    fun reSync(completeSync: Boolean = false) {
        if (syncInProgress.value) return
        if (completeSync) {
            println("start sync everything")
            syncEveryThing()
        } else {
            println("start re sync items")
            reSyncItems()
        }
    }

    private fun syncEveryThing() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateSyncProgress(true)
                updateError(syncError = false, errorMsg = "", errorTitle = "")
                val syncJob= listOf(
                    async {
                        val pendingCount = dataBaseRepository.getAllPendingSaleRecordsCount().first()
                        if (pendingCount <= 0) {
                            loadNextSalesInvoiceNumber()
                        } else {
                            updateSyncStatus("Invoice Number Error', 'Sync Pending Invoice First ")
                        }
                    },
                    async { _syncMember() },
                    async { _syncMemberGroup() },
                    async { _syncSalesHistory() },
                    async { _syncStockQuantity() },
                    async { _syncInventory() },
                    async { _syncCategories() },
                    async { _syncPromotion()},
                    async { _syncPaymentTypes()}
                )
                syncJob.awaitAll()

                println("All Sync Operations Completed Successfully")
                updateSyncStatus("All Sync Operations have been Completed Successfully")
                setLastSyncTs()
                updateSyncCountZero()
                updateSyncProgress(false)

            } catch (e: Exception) {
                val error = "failed during sync: ${e.message}"
                println("SYNC_ERROR $error")
                updateError(true, "SYNC_ERROR", error)
                updateSyncProgress(false)
            }
        }

    }


    // Start the periodic reSync timer with the given time
    private fun startPeriodicSync() {
        // Cancel any existing timer before setting a new one
        timerJob?.cancel()
        // Create a new coroutine for periodic resync
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) { // Check if the coroutine is still active
                val time = syncDataState.value.reSyncTime
                try {
                    reSyncItems(silent = true)
                    syncPendingSales()
                } catch (e: Exception) {
                    stopPeriodicSync()
                }
                delay(time * 60 * 1000L) // Convert time in minutes to milliseconds
            }
        }

        /*while (isActive) {
                reSyncItems(true)  // Perform the sync silently
                //getPendingData
                delay(timeInMinutes.minutes.inWholeMilliseconds)
            }*/
    }

    private fun reSyncItems(silent: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                networkRepository.syncAllApis(getBasicRequest()).collectLatest { apiResponse ->
                    observeResponseNew(apiResponse,
                        onLoading = {
                            if (!silent) {
                                updateSyncProgress(true)
                            }
                        },
                        onSuccess = { apiData ->
                            if (apiData.success) {
                                viewModelScope.launch {
                                    val unSyncList = apiData.result
                                    if (unSyncList != null) {
                                        updateSyncerGuid(apiData.result)
                                        getReSyncableItems()
                                        for ((key, value) in syncFlags) {
                                            updateSyncStatus("Syncing $key Data")
                                            println("Syncing $key Data")
                                            when (key) {
                                                MEMBER -> if (value) {
                                                    _syncMember()
                                                }
                                                MEMBER_GROUP -> if (value) _syncMemberGroup()
                                                PRODUCT -> if(value) {
                                                    _syncStockQuantity(lastSyncTime = getLastSyncDateTime())
                                                    _syncInventory(lastSyncTime = getLastSyncDateTime())
                                                }
                                                CATEGORY -> if(value) _syncCategories()
                                                PROMOTION -> if(value) _syncPromotion()
                                                PAYMENT_TYPE -> if(value) _syncPaymentTypes()
                                            }
                                            //updateSyncCount() // Increment after each sync
                                        }
                                        updateSyncCountZero()
                                        updateSyncStatus("All sync operation have been completed")
                                        println("All sync operation have been completed")
                                    }
                                }
                            }
                        },
                        onError = { errorMsg ->
                            handleError(SYNC_CHANGES_ERROR_TITLE, errorMsg)
                            updateSyncProgress(false)
                        }
                    )
                }
            } catch (e: Exception) {
                handleError(
                    errorTitle = SYNC_CHANGES_ERROR_TITLE,
                    errorMsg = e.message ?: "Unknown error"
                )
                updateSyncProgress(false)
            } finally {
                updateSyncProgress(false)
            }
        }
    }

    private suspend fun getReSyncableItems() {
        val state = syncDataState.value
        state.syncerGuid.items.forEach { element ->
            when (element.name.uppercase()) {
                MEMBER -> {
                    updateSyncStatus("Syncing Member")
                    syncFlags["MEMBER"] = getMemberSyncGrid() != element.syncerGuid

                }

                MEMBER_GROUP -> {
                    updateSyncStatus("Syncing Member Group")
                    syncFlags["MEMBERGROUP"] = getMemberGroupSyncGrid() != element.syncerGuid
                    return@forEach // Skips remaining lines of this iteration
                }

                "PRODUCT" -> {
                    updateSyncStatus("Syncing Inventory")
                    syncFlags["PRODUCT"] = getProductsSyncGrid() != element.syncerGuid
                    return@forEach
                }

                "CATEGORY" -> {
                    updateSyncStatus("Syncing Categories")
                    syncFlags["CATEGORY"] = getCategorySyncGrid() != element.syncerGuid
                }

                "MENU" -> {
                    updateSyncStatus("Syncing Menu")
                    syncFlags["MENU"] = getStockSyncGrid() != element.syncerGuid
                }

                "PROMOTION" -> {
                    updateSyncStatus("Syncing Promotions")
                    syncFlags["PROMOTION"] = getPromotionsSyncGrid() != element.syncerGuid
                }

                "PAYMENTTYPE" -> {
                    updateSyncStatus("Syncing Payment Type")
                    syncFlags["PAYMENTTYPE"] = getPaymentTypeSyncGrid() != element.syncerGuid
                }
            }
        }
    }

    private fun syncPendingSales() {
        viewModelScope.launch {
            try {
                // Launch all calls concurrently
                dataBaseRepository.getPosPendingSales().collect { pendingSale ->
                    val jobs = pendingSale.map { sale ->
                        async {
                            val posInvoice = deClassifyPendingRecord(
                                sale,
                                getLocation() ?: Location(),
                                getTenantId()
                            )
                            createUpdatePosInvoice(posInvoice, pendingSale.size, sale.id)
                        }
                    }
                    // Wait for all API calls to finish
                    jobs.awaitAll()
                    // Perform actions after all calls finish
                    listenSaleProcessState()
                }
            } catch (ex: Exception) {
                handleError(
                    "pending saving invoice", "Error saving invoice \n" +
                            " ${ex.message} "
                )
                updateInvoiceSyncing(false)
            }
        }
    }

    private suspend fun listenSaleProcessState() {
        posSaleApiState.collect { mApiStateResponse ->
            when (mApiStateResponse) {
                is ApiLoaderStateResponse.Loader -> {

                }

                is ApiLoaderStateResponse.Error -> {
                    updateInvoiceSyncing(false)
                }

                is ApiLoaderStateResponse.Success -> {
                    updateInvoiceSyncing(false)
                }

            }
        }
    }

    private suspend fun _syncMember() {
        try {
            networkRepository.getMembers(getBasicTenantRequest()).collectLatest { apiResponse ->
                observeResponseNew(apiResponse,
                    onLoading = {
                        updateSyncStatus("Syncing Member Data")
                    },
                    onSuccess = { apiData ->
                        if (apiData.success) {
                            viewModelScope.launch {
                                dataBaseRepository.insertMembers(apiData)
                                val syncItem = updateSyncGrid(MEMBER)
                                preferencesRepository.setMemberSyncGrid(syncItem.syncerGuid)
                                updateSyncCount()
                                println("SyncMemberInsertionComplete ${syncDataState.value.syncCount}")
                            }
                        }
                    },
                    onError = { errorMsg ->
                        updateError(true, MEMBER_ERROR_TITLE, errorMsg)
                    }
                )
            }
        } catch (e: Exception) {
            val error = "${e.message}"
            updateError(true, MEMBER_ERROR_TITLE, error)
        }
    }

    private suspend fun _syncMemberGroup() {
        try {
            networkRepository.getMemberGroup(getBasicTenantRequest()).collectLatest { apiResponse ->
                observeResponseNew(apiResponse,
                    onLoading = {
                        updateSyncStatus("Syncing Member Group Data")
                    },
                    onSuccess = { apiData ->
                        if (apiData.success) {
                            viewModelScope.launch {
                                dataBaseRepository.insertMemberGroup(apiData)
                                val syncItem = updateSyncGrid(MEMBER_GROUP)
                                preferencesRepository.setMemberGroupSyncGrid(syncItem.syncerGuid)
                                updateSyncCount()
                                println("SyncMemberGroupInsertionComplete ${syncDataState.value.syncCount}")
                            }
                        }
                    },
                    onError = { errorMsg ->
                        handleError(MEMBER_ERROR_TITLE, errorMsg)
                    }
                )
            }
        } catch (e: Exception) {
            val error = "${e.message}"
            handleError(MEMBER_ERROR_TITLE, error)
        }
    }

    private suspend fun _syncSalesHistory() {
        try {
            getLatestSale(skipCount = 0, maxResultCount = 1).collect { sale ->
                when (sale) {
                    is RequestState.Error -> {
                        updateError(true, SYNC_SALES_ERROR_TITLE, sale.message)
                    }

                    is RequestState.Idle -> {

                    }

                    is RequestState.Loading -> {
                        updateLoaderMsg("Syncing Sales History")
                    }

                    is RequestState.Success -> {
                        val totalCount = sale.data.result?.totalCount
                        if (totalCount != null && totalCount >= 1000) {
                            getLatestSale(
                                skipCount = totalCount - 600,
                                maxResultCount = 1000
                            ).collect { response ->
                                observePosInvoiceSales(response)
                            }
                        } else {
                            observePosInvoiceSales(sale)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            val error = "${e.message}"
            updateError(true, SYNC_SALES_ERROR_TITLE, error)
        }
    }

    private suspend fun getLatestSale(
        skipCount: Long?,
        maxResultCount: Int?
    ): Flow<RequestState<GetPosInvoiceResult>> {
        return networkRepository.getLatestSales(
            POSInvoiceRequest(
                locationId = getLocationId(),
                maxResultCount = maxResultCount ?: 1000,
                skipCount = skipCount?.toInt() ?: 0,
                sorting = "Id"
            )
        )
    }

    private fun observePosInvoiceSales(apiResponse: RequestState<GetPosInvoiceResult>) {
        observeResponseNew(apiResponse,
            onLoading = {
                //updateSyncProgress(true)
            },
            onSuccess = { apiData ->
                if (apiData.success) {
                    viewModelScope.launch {
                        dataBaseRepository.insertNewLatestSales(apiData)
                        updateSyncGrid(INVOICE)
                        updateSyncCount()
                        println("SyncInvoiceSalesInsertionComplete ${syncDataState.value.syncCount}")
                    }
                }
            },
            onError = { errorMsg ->
                updateError(true, SYNC_SALES_ERROR_TITLE, errorMsg)
            }
        )

    }

    private suspend fun _syncStockQuantity(lastSyncTime: String? = null){
        try {
            networkRepository.getProductLocation( BasicApiRequest(locationId = getLocationId(),
            tenantId = getTenantId(),
            lastSyncDateTime = lastSyncTime)).collectLatest {stockAvailResponse->
                observeResponseNew(stockAvailResponse,
                    onLoading = {
                        updateSyncStatus("Syncing Inventory Count")
                    },
                    onSuccess = { apiData ->
                        if (apiData.success) {
                            val updatedStockQtyMap: MutableMap<Int, Double?> = mutableMapOf()
                            apiData.result?.items?.forEach { stock ->
                                updatedStockQtyMap[stock.productId] = stock.qtyOnHand
                            }
                            stockQtyMap.update { oldMap -> oldMap + updatedStockQtyMap }
                        }
                    },
                    onError = { errorMsg ->
                        updateError(true,INVENTORY_ERROR_TITLE, errorMsg)

                    }
                )
            }
        }catch (e: Exception){
            val error="${e.message}"
            updateError(true,INVENTORY_ERROR_TITLE,error)
        }
    }

    private suspend fun _syncInventory(syncProduct:Boolean=true,lastSyncTime:String?=null){
        try {
            if(!syncProduct) return
           val request = BasicApiRequest(
                locationId = getLocationId(),
                tenantId = getTenantId(),
                lastSyncDateTime = lastSyncTime)
            val inventoryResponse=networkRepository.getProductsWithTax(request)
            val barcodesResponse=networkRepository.getProductBarCode(request)
            if(lastSyncTime!=null){
                networkRepository.getProductBarCode(request.copy(isDeleted = true))
                dataBaseRepository.clearBarcode()
            }
            observeResponse(inventoryResponse,
                onLoading = {
                    updateSyncStatus("Syncing Inventory")
                },
                onSuccess = { apiData ->
                    viewModelScope.launch {
                        if(apiData.success){
                            dataBaseRepository.insertUpdateInventory(apiData,lastSyncTime, stockQtyMap.value)
                            val syncItem = updateSyncGrid(PRODUCT)
                            preferencesRepository.setProductsSyncGrid(syncItem.syncerGuid)
                            updateSyncCount()
                            println("SyncProductInsertionComplete ${syncDataState.value.syncCount}")
                        }
                    }
                },
                onError = { errorMsg ->
                    updateError(true,INVENTORY_ERROR_TITLE,errorMsg)
                }
            )

            observeResponse(barcodesResponse,
                onLoading = {
                    updateSyncStatus("Syncing Barcode")
                },
                onSuccess = { apiData ->
                    viewModelScope.launch {
                        if(apiData.success){
                            dataBaseRepository.insertUpdateBarcode(apiResponse = apiData,lastSyncDateTime=lastSyncTime)
                        }
                    }
                },
                onError = { errorMsg ->
                    updateError(true,INVENTORY_ERROR_TITLE,errorMsg)
                }
            )

        }catch (e: Exception){
            val error="${e.message}"
            updateError(true,INVENTORY_ERROR_TITLE,error)
        }
    }

    private suspend fun _syncCategories(){
        try {
            networkRepository.getMenuCategories(getBasicRequest()).collectLatest {apiResponse->
                observeResponseNew(apiResponse,
                    onLoading = {
                        updateSyncStatus("Syncing Categories")
                    },
                    onSuccess = { apiData ->
                        if(apiData.success){
                            viewModelScope.launch {
                                dataBaseRepository.insertCategories(apiData)
                                val syncItem = updateSyncGrid(CATEGORY)
                                preferencesRepository.setCategorySyncGrid(syncItem.syncerGuid)
                                updateSyncCount()
                                println("SyncCategoryInsertionComplete ${syncDataState.value.syncCount}")
                                categoryList.update { apiData.result.items }
                                _syncMenu(apiData.result.items)
                            }
                        }
                    },
                    onError = {
                            errorMsg ->
                        updateError(true,MENU_CATEGORY_ERROR_TITLE,errorMsg)
                    }
                )
            }
        }catch (e: Exception){
            val error="${e.message}"
            updateError(true,MENU_CATEGORY_ERROR_TITLE,error)
        }
    }

    private suspend fun _syncMenu(items: List<CategoryItem>) {
        //val newStock : MutableList<MenuItem> = mutableListOf()
        items.forEach { cat->
            println("MenuProductsAPI : ${cat.id}")
            networkRepository.getMenuProducts(getBasicRequest(cat.id?:0)).collectLatest {response->
                observeResponseNew(response,
                    onLoading = {
                        updateSyncStatus("Syncing Menu For Category ID ${cat.id}")
                    },
                    onSuccess = { menuData ->
                        if(menuData.success){
                            viewModelScope.launch {
                                val newStock = menuData.result.items.map { menu->
                                    menu.copy(menuCategoryId=cat.id?:0, id = if(menu.id==0L) menu.productId else menu.id)
                                    //newStock.add(updatedMenu)
                                }
                                dataBaseRepository.insertNewStock(newStock.map{mnu->
                                    Stock(
                                        id = mnu.id?:0,
                                        name = mnu.name?:"",
                                        imagePath = mnu.imagePath ?: "",
                                        categoryId = mnu.menuCategoryId?:0,
                                        productId = mnu.productId?:0,
                                        sortOrder = mnu.sortOrder?:0,
                                        inventoryCode = mnu.inventoryCode?:"",
                                        fgColor = mnu.foreColor ?: "",
                                        bgColor = mnu.backColor ?: "",
                                        barcode = mnu.barCode?:""
                                    )
                                })

                                val syncItem = updateSyncGrid(MENU)
                                preferencesRepository.setStockSyncGrid(syncItem.syncerGuid)
                                updateSyncCount()
                                println("SyncStockInsertionComplete ${syncDataState.value.syncCount}")
                            }
                        }
                    },
                    onError = { errorMsg ->
                        updateError(true,MENU_PRODUCTS_ERROR_TITLE,errorMsg)
                    }
                )
            }
        }
    }

    private suspend fun _syncPromotion(){
        try {
            networkRepository.getPromotions(PromotionRequest(tenantId = getTenantId())).collectLatest { apiResponse->
                observeResponseNew(apiResponse,
                    onLoading = {
                        updateSyncStatus("Syncing Promotions")
                    },
                    onSuccess = { apiData ->
                        if(apiData.success){
                            viewModelScope.launch {
                                dataBaseRepository.insertPromotions(apiData)
                                //get
                                apiData.result?.map {item->
                                    if(item!=null){
                                        when(item.promotionTypeName){
                                            "PromotionByQty"->{
                                                networkRepository.getPromotionsByQty(PromotionRequest(id = item.id.toInt())).collect { promotionsData->
                                                    observePromotionsQty(promotionsData,item)
                                                }
                                            }

                                            "PromotionByPrice" ->{
                                                networkRepository.getPromotionsByPrice(PromotionRequest(id = item.id.toInt())).collect { promotionsData->
                                                    observePromotionPrice(promotionsData,item)
                                                }
                                            }

                                            "PromotionByPriceBreak"->{
                                                networkRepository.getPromotionsByQty(PromotionRequest(id = item.id.toInt())).collectLatest { promotionsData->
                                                    observePromotionByPriceBreak(promotionsData,item)
                                                }
                                            }
                                            else->{
                                                networkRepository.getPromotionsByQty(PromotionRequest(id = item.id.toInt())).collectLatest { promotionsData->
                                                    observeDefaultPromotions(promotionsData,item)
                                                }
                                            }
                                        }

                                    }
                                }

                                val syncItem = updateSyncGrid(PROMOTION)
                                preferencesRepository.setPromotionsSyncGrid(syncItem.syncerGuid)
                                updateSyncCount()
                                println("SyncPromotionInsertionComplete ${syncDataState.value.syncCount}")
                            }
                        }
                    },
                    onError = {
                            errorMsg ->
                        updateError(errorTitle = PROMOTIONS_ERROR_TITLE, errorMsg = errorMsg)
                    }
                )
            }
        }catch (e: Exception){
            val error="${e.message}"
            updateError(true,PROMOTIONS_ERROR_TITLE,error)
        }
    }

    private fun observePromotionsQty(
        apiResponse: RequestState<GetPromotionsByQtyResult>,
        promotion: PromotionItem
    ){
        observeResponseNew(apiResponse,
            onLoading = { updateSyncStatus("Syncing Promotion QTY....")},
            onSuccess = { apiData ->
                if(apiData.success){
                    viewModelScope.launch {
                        apiData.result?.forEach { item ->
                            dataBaseRepository.insertPromotionDetails(
                                PromotionDetails(
                                    id=item.productId?.toLong()?:0,
                                    promotionId = item.promotionId?:0,
                                    productId = item.productId?:0,
                                    inventoryCode = item.inventoryCode?:"",
                                    promotionTypeName = promotion.promotionTypeName?:"",
                                    price = item.price?:0.0,
                                    promotionPrice = item.price?:0.0,
                                    promotionPerc = promotion.discountPercentage?.takeIf { it > 0.00 },
                                    qty = promotion.qty?:0.0,
                                    amount = promotion.amount?:0.0)

                            )
                        }
                    }
                }
            },
            onError = { errorMsg ->
                updateError(true,PROMOTIONS_ERROR_TITLE,errorMsg)
            }
        )
    }

    private fun observePromotionPrice(
        apiResponse: RequestState<GetPromotionsByPriceResult>,
        promotion: PromotionItem
    ){
        observeResponseNew(apiResponse,
            onLoading = { updateSyncStatus("Syncing Promotion By Price....")},
            onSuccess = { apiData ->
                if(apiData.success){
                    viewModelScope.launch {
                        apiData.result?.forEach { element ->
                            dataBaseRepository.insertPromotionDetails(
                                PromotionDetails(
                                    id=element.productId.toLong(),
                                    promotionId = element.promotionId,
                                    productId = element.productId,
                                    inventoryCode = element.inventoryCode,
                                    promotionTypeName = promotion.promotionTypeName?:"",
                                    price = element.price,
                                    promotionPrice = element.promotionPrice,
                                    promotionPerc = element.promotionPerc,
                                    qty = promotion.qty?:0.0)

                            )
                        }
                    }
                }
            },
            onError = { errorMsg ->
                updateError(true,PROMOTIONS_ERROR_TITLE,errorMsg)
            }
        )
    }


    private fun observePromotionByPriceBreak(
        apiResponse: RequestState<GetPromotionsByQtyResult>,
        promotion: PromotionItem
    ){
        observeResponseNew(apiResponse,
            onLoading = { updateSyncStatus("Syncing Promotion By Price Break....")},
            onSuccess = { apiData ->
                if(apiData.success){
                    viewModelScope.launch {
                        apiData.result?.forEach { element ->
                            dataBaseRepository.insertPromotionDetails(
                                PromotionDetails(
                                    id=element.productId?.toLong()?:0,
                                    promotionId = element.promotionId?:0,
                                    productId = element.productId?:0,
                                    inventoryCode = element.inventoryCode?:"",
                                    promotionTypeName = promotion.promotionTypeName?:"",
                                    price = element.price?:0.0,
                                    promotionPrice = element.price?:0.0,
                                    qty = promotion.qty?:0.0,
                                    amount = promotion.amount?:0.0,
                                    priceBreakPromotionAttribute= parsePriceBreakPromotionAttributes(promotion.priceBreakPromotionAttribute)
                                )

                            )
                        }
                    }
                }
            },
            onError = { errorMsg ->
                updateError(true,PROMOTIONS_ERROR_TITLE,errorMsg)
            }
        )
    }

    private fun observeDefaultPromotions(
        apiResponse: RequestState<GetPromotionsByQtyResult>,
        promotion: PromotionItem
    ){
        observeResponseNew(apiResponse,
            onLoading = { updateSyncStatus("Syncing Promotion Default....")},
            onSuccess = { apiData ->
                if(apiData.success){
                    viewModelScope.launch {
                        apiData.result?.forEach { item ->
                            dataBaseRepository.insertPromotionDetails(
                                PromotionDetails(
                                    id = item.productId?.toLong()?:0 ,
                                    promotionId = item.promotionId?:0,
                                    productId = item.productId?:0,
                                    inventoryCode = item.inventoryCode?:"",
                                    promotionTypeName = promotion.promotionTypeName?:"",
                                    price = item.price?:0.0
                                )

                            )
                        }
                    }
                }
            },
            onError = { errorMsg ->
                updateError(true,PROMOTIONS_ERROR_TITLE,errorMsg)
            }
        )
    }

    private suspend fun _syncPaymentTypes(){
        try {
            networkRepository.getPaymentTypes(getBasicTenantRequest()).collectLatest {apiResponse->
                observeResponseNew(apiResponse,
                    onLoading = {
                        updateSyncStatus("Syncing Payment Type")
                    },
                    onSuccess = { apiData ->
                        if(apiData.success){
                            viewModelScope.launch {
                                dataBaseRepository.insertPaymentType(apiData)
                                val syncItem = updateSyncGrid(PAYMENT_TYPE)
                                preferencesRepository.setPaymentTypeSyncGrid(syncItem.syncerGuid)
                                updateSyncCount()
                                println("SyncPaymentInsertionComplete ${syncDataState.value.syncCount}")
                            }
                        }
                    },
                    onError = {
                            errorMsg ->
                        updateError(true,PAYMENT_TYPE_ERROR_TITLE,errorMsg)
                    }
                )
            }
        }catch (e: Exception){
            val error="${e.message}"
            updateError(true,PAYMENT_TYPE_ERROR_TITLE,error)
        }
    }

    private suspend fun loadNextSalesInvoiceNumber(){
        try {
            updateSyncStatus("syncing sale invoice count")
            networkRepository.getNextPOSSaleInvoice(getBasicRequest()).collectLatest {apiResponse->
                observeResponseNew(apiResponse,
                    onLoading = {
                        updateSyncProgress(true)
                    },
                    onSuccess = { apiData ->
                        viewModelScope.launch {
                            if(apiData.success){
                                apiData.result?.let { result->
                                    preferences.setNextSalesInvoiceNumber(result.invoiceNo?:"")
                                    preferences.setSalesInvoicePrefix(result.invoicePrefix?:"")
                                    preferences.setSalesInvoiceNoLength(result.posLength?:0)
                                }
                            }
                        }

                    },
                    onError = { errorMsg ->
                        updateError(true,NEXT_SALE_ERROR_TITLE,errorMsg)
                    }
                )
            }
        }catch (e: Exception){
            val error="${e.message}"
            updateError(true, NEXT_SALE_ERROR_TITLE,error)
        }
    }
    
    private fun updateSyncGrid(name: String): SyncItem {
        val state=syncDataState.value
        return state.syncerGuid.items.firstOrNull{ it.name.uppercase() == name }
            ?: SyncItem(name = name, syncerGuid = "", id = 0)
    }

    private suspend fun setLastSyncTs(){
        preferences.setLastSyncTs(getCurrentDateAndTimeInEpochMilliSeconds())
    }

    private suspend fun setReSyncTimer(time:Int) {
        preferencesRepository.setReSyncTimer(time)
    }

    // Load from preferencesRepository
    private suspend fun getReSyncTimer(): Int {
        return preferencesRepository.getReSyncTime().first()
    }

    // Update reSync time and restart timer
    fun updateReSyncTime(time: Int) {
        if (time > 0) {
            viewModelScope.launch {
                setReSyncTimer(time) // Persist new time to storage
                _syncDataState.update {it.copy(reSyncTime = time)}
                startPeriodicSync()  // Restart timer with new interval
            }
        }
    }

    private fun updateSyncerGuid(mUnSyncList: UnSyncList) {
        _syncDataState.update { it.copy(syncerGuid = mUnSyncList) }
    }

    private fun updateInvoiceSyncing(syncStatus: Boolean) {
        _syncDataState.update { it.copy(syncingPosInvoices = syncStatus) }
    }

    private fun updateSyncProgress(syncStatus: Boolean) {
        _syncDataState.update { it.copy(syncInProgress = syncStatus) }
    }

    private fun updateSyncStatus(syncStatus: String) {
        _syncDataState.update { it.copy(syncProgressStatus = syncStatus) }
    }

    // Increment the sync count
    private fun updateSyncCount() {
        _syncDataState.update { it.copy(syncCount = it.syncCount + 1) }
    }
    private fun updateSyncCountZero() {
        _syncDataState.update { it.copy(syncCount = 0) }
    }


    // Handle any sync errors
    private fun handleError(errorTitle: String, errorMsg: String) {
        _syncDataState.update { it.copy(syncError = true, syncErrorInfo = "$errorTitle \n $errorMsg") }
    }

    private fun updateError(syncError:Boolean=true,errorTitle: String, errorMsg: String) {
        _syncDataState.update { it.copy(syncError = syncError,syncErrorInfo = "$errorTitle \n $errorMsg") }
    }

     /*suspend fun getBasicRequest() = BasicApiRequest(
        tenantId = preferencesRepository.getTenantId().first(),
        locationId = preferencesRepository.getLocationId().first(),
    )*/


    private suspend fun getBasicTenantRequest() = BasicApiRequest(
        tenantId = preferencesRepository.getTenantId().first()
    )

    private suspend fun getMemberSyncGrid() : String{
        return preferencesRepository.getMemberSyncGrid().first()
    }

    private suspend fun getMemberGroupSyncGrid() : String{
        return preferencesRepository.getMemberGroupSyncGrid().first()
    }

    private suspend fun getProductsSyncGrid() : String{
        return preferencesRepository.getProductsSyncGrid().first()
    }

    private suspend fun getCategorySyncGrid() : String{
        return preferencesRepository.getCategorySyncGrid().first()
    }

    private suspend fun getStockSyncGrid() : String{
        return preferencesRepository.getStockSyncGrid().first()
    }

    private suspend fun getPromotionsSyncGrid() : String{
        return preferencesRepository.getPromotionsSyncGrid().first()
    }

    private suspend fun getPaymentTypeSyncGrid() : String{
        return preferencesRepository.getPaymentTypeSyncGrid().first()
    }


    private fun stopPeriodicSync() {
        timerJob?.cancel()  // Stop the job
    }

    override fun onCleared() {
        super.onCleared()
        stopPeriodicSync()  // Cancel sync job when ViewModel is cleared
    }
}