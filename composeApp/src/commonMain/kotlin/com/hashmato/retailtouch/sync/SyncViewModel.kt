package com.hashmato.retailtouch.sync

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.viewModelScope
import com.hashmato.retailtouch.domain.ApiUtils
import com.hashmato.retailtouch.domain.ApiUtils.observeResponse
import com.hashmato.retailtouch.domain.ApiUtils.observeResponseNew
import com.hashmato.retailtouch.domain.RequestState
import com.hashmato.retailtouch.domain.model.ApiLoaderStateResponse
import com.hashmato.retailtouch.domain.model.basic.BasicApiRequest
import com.hashmato.retailtouch.domain.model.invoiceSaleTransactions.GetPosInvoiceResult
import com.hashmato.retailtouch.domain.model.invoiceSaleTransactions.POSInvoiceRequest
import com.hashmato.retailtouch.domain.model.location.Location
import com.hashmato.retailtouch.domain.model.login.LoginRequest
import com.hashmato.retailtouch.domain.model.menu.CategoryItem
import com.hashmato.retailtouch.domain.model.menu.MenuItem
import com.hashmato.retailtouch.domain.model.products.Stock
import com.hashmato.retailtouch.domain.model.promotions.GetPromotionsByPriceResult
import com.hashmato.retailtouch.domain.model.promotions.GetPromotionsByQtyResult
import com.hashmato.retailtouch.domain.model.promotions.PromotionDetails
import com.hashmato.retailtouch.domain.model.promotions.PromotionItem
import com.hashmato.retailtouch.domain.model.promotions.PromotionRequest
import com.hashmato.retailtouch.domain.model.sync.SyncItem
import com.hashmato.retailtouch.domain.model.sync.UnSyncList
import com.hashmato.retailtouch.presentation.viewModels.BaseViewModel
import com.hashmato.retailtouch.utils.AppConstants.CATEGORY
import com.hashmato.retailtouch.utils.AppConstants.INVENTORY_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppConstants.INVOICE
import com.hashmato.retailtouch.utils.AppConstants.MEMBER
import com.hashmato.retailtouch.utils.AppConstants.MEMBER_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppConstants.MEMBER_GROUP
import com.hashmato.retailtouch.utils.AppConstants.MENU
import com.hashmato.retailtouch.utils.AppConstants.MENU_CATEGORY_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppConstants.MENU_PRODUCTS_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppConstants.NEXT_SALE_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppConstants.PAYMENT_TYPE
import com.hashmato.retailtouch.utils.AppConstants.PAYMENT_TYPE_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppConstants.PRODUCT
import com.hashmato.retailtouch.utils.AppConstants.PROMOTION
import com.hashmato.retailtouch.utils.AppConstants.PROMOTIONS_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppConstants.SYNC_CHANGES_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppConstants.SYNC_SALES_ERROR_TITLE
import com.hashmato.retailtouch.utils.ConnectivityObserver
import com.hashmato.retailtouch.utils.DateFormatter
import com.hashmato.retailtouch.utils.DateTimeUtils.getLastSyncDateTime
import com.hashmato.retailtouch.utils.PrefKeys.TOKEN_EXPIRY_THRESHOLD
import com.hashmato.retailtouch.utils.ToastManager
import com.hashmato.retailtouch.utils.serializers.db.parsePriceBreakPromotionAttributes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
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
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent

class SyncViewModel : BaseViewModel() , KoinComponent {

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
            reSync()
        }
    }

   /* suspend fun reSyncAfterLogin(locationChange : Boolean = false)  {
        val pCount = dataBaseRepository.getAllPendingSaleRecordsCount().first()
        println("pCount :$pCount")
        if (pCount <= 0 || locationChange) {
            println("start syncEveryThing")
            syncEveryThing()
        } else {
            println("start reSyncItems")
            reSyncItems()
        }
    }*/

    fun startCompleteSync() {
        if (_syncDataState.value.syncInProgress) return
         println("start sync everything")
         syncEveryThing()
    }

     fun reSync(completeSync: Boolean = false) {
        if (_syncDataState.value.syncInProgress) return

         startPeriodicSync()
       /* if (completeSync) {
            println("start sync everything")
            syncEveryThing()
        } else {
            println("start reSyncItems")
            startPeriodicSync()
        }*/
    }



    private fun syncEveryThing() {
        viewModelScope.launch(Dispatchers.IO) {
            val isConnected = ConnectivityObserver.isConnected.first()
            if(isConnected){
                try {
                    updateSyncProgress(true)
                    updateSyncCompleteStatus(false)
                    updateError(syncError = false, errorMsg = "", errorTitle = "")

                    if (loginRequired()) {
                        refreshToken()
                    }
                    val syncJob= listOf(
                        async {
                            val pendingCount = dataBaseRepository.getAllPendingSaleRecordsCount().first()
                            if (pendingCount <= 0) {
                                _loadNextSalesInvoiceNumber()
                            } else {
                                updateSyncStatus("Invoice Number Error', 'Sync Pending Invoice First ")
                            }
                        },
                        async { _syncMember() },
                        async { _syncMemberGroup() },
                        async { _syncSalesHistory() },
                        async { _syncCategory() },
                        async { _syncInventory() },
                        async { _syncPromotion()},
                        async { _syncPaymentTypes()}
                    )
                    syncJob.awaitAll()

                    println("All Sync Operations Completed Successfully")
                    updateSyncStatus("All Sync Operations have been Completed Successfully")
                    setLastSyncTs()
                    updateSyncCountZero()
                    updateSyncCompleteStatus(true)
                    updateSyncProgress(false)
                } catch (e: Exception) {
                    val error = "failed during sync: ${e.message}"
                    println("SYNC_ERROR $error")
                    updateError(true, "SYNC_ERROR", error)
                    updateSyncProgress(false)
                    updateSyncCompleteStatus(false)
                }
            }else{
                ToastManager.showToast("No Internet Connection Found")
            }

        }
    }

     private suspend fun loginRequired() : Boolean {
        // println("LoginRequired")
        val tokenTime : Long = preferences.getTokenTime().first()
        val currentTime = DateFormatter().getCurrentDateAndTimeInEpochMilliSeconds() /*getCurrentDateAndTimeInEpochMilliSeconds()*/
        val hoursPassed = DateFormatter().getHoursDifferenceFromEpochMilliseconds(tokenTime, currentTime)
         println("hoursPassed $hoursPassed")
        return hoursPassed >= TOKEN_EXPIRY_THRESHOLD
    }

    private suspend fun refreshToken(): String {
        return withContext(Dispatchers.IO) { // Use withContext instead of runBlocking
            try {
                var result = ""
                 networkRepository.hitLoginAPI(getLoginDetails()).collect { response ->
                    when (response) {
                        is RequestState.Success -> {
                            val token = response.data.result
                            ApiUtils.preferences.setToken(token ?: "")
                            result = token ?: ""
                        }
                        else -> {

                        }
                    }
                }
                result
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }
    }

    private suspend fun getLoginDetails(): LoginRequest {
        val loginRequest = LoginRequest(
            usernameOrEmailAddress = ApiUtils.preferences.getUserName().first(),
            tenancyName = ApiUtils.preferences.getTenancyName().first(),
            password = ApiUtils.preferences.getUserPass().first(),
        )
        return loginRequest

    }

    // Update reSync time and restart timer
    fun updateReSyncTime(time: Int) {
        if (time > 0) {
            viewModelScope.launch {
                setReSyncTimer(time)
                startPeriodicSync()
            }
        }
    }

    // Start the periodic reSync timer with the given time
    private fun startPeriodicSync() {
        println("Start Periodic Sync")
        // Cancel any existing timer before setting a new one
        timerJob?.cancel()
        // Create a new coroutine for periodic re sync
        timerJob = CoroutineScope(Dispatchers.IO+SupervisorJob()).launch {
            while (isActive) { // Check if the coroutine is still active
                try {
                    val interval = getReSyncTimer() // Fetch the timer dynamically
                    if (interval <= 0) {
                        throw IllegalStateException("Invalid sync interval from getReSyncTimer()")
                    }
                    reSyncItems(silent = true)
                    delay(interval * 60 * 1000L) // Convert time in minutes to milliseconds
                } catch (e: Exception) {
                    stopPeriodicSync()
                }
            }
        }

    }

    private fun reSyncItems(silent: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (!silent) {
                    updateSyncProgress(true)
                }
                if (loginRequired()){
                    println("LoginRequired")
                    refreshToken()
                }
                syncPendingSales()
                //refreshToken()
                networkRepository.syncAllApis(getBasicRequest()).collect { apiResponse ->
                    observeResponseNew(apiResponse,
                        onLoading = {
                        },
                        onSuccess = { apiData ->
                            if (apiData.success) {
                                viewModelScope.launch {
                                    val unSyncList = apiData.result
                                    if (unSyncList != null) {
                                        updateSyncerGuid(apiData.result)
                                        getReSyncableItems()
                                        for ((key, value) in syncFlags) {
                                            println("Syncing $key ...")
                                            updateSyncStatus("Syncing $key ...")
                                            when (key) {
                                                MEMBER ->  if(value){ _syncMember() }
                                                MEMBER_GROUP -> if(value) _syncMemberGroup()
                                                PRODUCT -> if(value) { _syncInventory(lastSyncTime = getLastSyncDateTime()) }
                                                CATEGORY -> if(value)  _syncCategory()
                                                PROMOTION -> if(value)  _syncPromotion()
                                                PAYMENT_TYPE -> if(value)  _syncPaymentTypes()
                                            }
                                        }
                                        updateSyncCountZero()
                                        updateSyncCompleteStatus(true)
                                        updateSyncStatus("All sync operation have been completed")
                                        println("All sync operation have been completed")
                                    }
                                }
                            }
                        },
                        onError = { errorMsg ->
                            handleError(SYNC_CHANGES_ERROR_TITLE, errorMsg)
                            updateSyncProgress(false)
                            updateSyncCompleteStatus(false)
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
                updateSyncCompleteStatus(false)
            }
        }
    }

    private suspend fun getReSyncableItems() {
        val state = syncDataState.value
        state.syncerGuid.items.forEach { element ->
            when (element.name.uppercase()) {
                MEMBER -> {
                    updateSyncStatus("Syncing Member")
                    println("MEMBER ${getMemberSyncGrid()}")
                    syncFlags["MEMBER"] = getMemberSyncGrid() != element.syncerGuid

                }

                MEMBER_GROUP -> {
                    updateSyncStatus("Syncing Member Group")
                    println("MEMBERGROUP ${getMemberGroupSyncGrid()}")
                    syncFlags["MEMBERGROUP"] = getMemberGroupSyncGrid() != element.syncerGuid
                }

                "PRODUCT" -> {
                    updateSyncStatus("Syncing Inventory")
                    println("PRODUCT ${getProductsSyncGrid()}")
                    syncFlags["PRODUCT"] = getProductsSyncGrid() != element.syncerGuid
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
                    println("PAYMENTTYPE ${getPaymentTypeSyncGrid()}")
                    syncFlags["PAYMENTTYPE"] = getPaymentTypeSyncGrid() != element.syncerGuid
                }
            }
        }
    }

    private suspend fun syncPendingSales() {
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
                handleError("pending saving invoice", "Error saving invoice \n" +
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
            updateSyncStatus("Syncing Member Data...")
            networkRepository.getMembers(getBasicTenantRequest()).collectLatest { apiResponse ->
                observeResponseNew(apiResponse,
                    onLoading = {},
                    onSuccess = { apiData ->
                        if (apiData.success) {
                            viewModelScope.launch {
                                dataBaseRepository.insertMembers(apiData)
                                val syncItem = updateSyncGrid(MEMBER)
                                preferences.setMemberSyncGrid(syncItem.syncerGuid)
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
            updateSyncStatus("Syncing Member Group Data")
            networkRepository.getMemberGroup(getBasicTenantRequest()).collect { apiResponse ->
                observeResponseNew(apiResponse,
                    onLoading = {},
                    onSuccess = { apiData ->
                        if (apiData.success) {
                            viewModelScope.launch {
                                dataBaseRepository.insertMemberGroup(apiData)
                                val syncItem = updateSyncGrid(MEMBER_GROUP)
                                preferences.setMemberGroupSyncGrid(syncItem.syncerGuid)
                                updateSyncCount()
                                println("SyncMemberGroupInsertionComplete ${syncDataState.value.syncCount}")
                            }
                        }
                    },
                    onError = { errorMsg ->
                        updateError(true,MEMBER_ERROR_TITLE, errorMsg)
                    }
                )
            }
        } catch (e: Exception) {
            val errorMsg = "${e.message}"
            updateError(true,MEMBER_ERROR_TITLE, errorMsg)
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

    private suspend fun _syncProducts(syncProduct:Boolean=true,lastSyncTime:String?=null){
        try {
            if(!syncProduct) return
            updateSyncStatus("Syncing Inventory...")
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
                onLoading = {},
                onSuccess = { apiData ->
                    viewModelScope.launch {
                        if(apiData.success){
                            dataBaseRepository.insertUpdateInventory(apiData,lastSyncTime, stockQtyMap.value)
                            val syncItem = updateSyncGrid(PRODUCT)
                            preferences.setProductsSyncGrid(syncItem.syncerGuid)
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

    private fun _syncInventory(syncProduct:Boolean=true,lastSyncTime:String?=null){
        try {
            if(!syncProduct) return
            updateLoginSyncStatus("Syncing Inventory...")
            println("Syncing Inventory : ${count++}")
            viewModelScope.launch {
                val stockQtyMap: MutableMap<Int, Double?> = mutableMapOf()
                val request = BasicApiRequest(
                    locationId = getLocationId(),
                    tenantId = getTenantId(),
                    lastSyncDateTime = lastSyncTime)
                async { networkRepository.getProductLocation(request).collect {stockAvailResponse->
                    observeResponseNew(stockAvailResponse,
                        onLoading = {
                        },
                        onSuccess = { apiData ->
                            if (apiData.success) {
                                apiData.result?.items?.forEach { stock ->
                                    stockQtyMap[stock.productId]=stock.qtyOnHand
                                }
                            }
                        },
                        onError = { errorMsg ->
                            updateError(true,INVENTORY_ERROR_TITLE,errorMsg)
                        }
                    )
                } }.await()
                println("StocksQty:$stockQtyMap")
                async {
                    networkRepository.getProductsWithTax(request).collect{inventoryResponse->
                        observeResponseNew(inventoryResponse,
                            onLoading = {  },
                            onSuccess = { apiData ->
                                if(apiData.success){
                                    viewModelScope.launch {
                                        dataBaseRepository.insertUpdateInventory(apiData,lastSyncTime, stockQtyMap)
                                        val syncItem = updateSyncGrid(PRODUCT)
                                        preferences.setProductsSyncGrid(syncItem.syncerGuid)
                                        updateSyncCount()
                                        println("SyncProductInsertionComplete ${syncDataState.value.syncCount}")
                                    }
                                }
                            },
                            onError = {
                                    errorMsg ->
                                updateError(true,INVENTORY_ERROR_TITLE,errorMsg)
                            }
                        )
                    }
                }.await()
                async {
                    networkRepository.getProductBarCode(request).collect{barcodesResponse->
                        observeResponseNew(barcodesResponse,
                            onLoading = {  },
                            onSuccess = { apiData ->
                                viewModelScope.launch {
                                    dataBaseRepository.insertUpdateBarcode(apiResponse = apiData,lastSyncDateTime=lastSyncTime)
                                }
                            },
                            onError = { errorMsg ->
                                updateError(true,INVENTORY_ERROR_TITLE,errorMsg)
                            }
                        )
                    }
                }.await()

            }

        }catch (e: Exception){
            val errorMsg="${e.message}"
            updateError(true,INVENTORY_ERROR_TITLE,errorMsg)
        }
    }

    private suspend fun _syncCategory(){
        try {
            viewModelScope.launch {
                val categoryList: MutableList<CategoryItem> = mutableListOf()
                async {
                    updateLoginSyncStatus("Syncing Product Category...")
                    println("Syncing Product Category : ${count++}")
                    networkRepository.getMenuCategories(getBasicRequest()).collect {apiResponse->
                        observeResponseNew(apiResponse,
                            onLoading = {  },
                            onSuccess = { apiData ->
                                if(apiData.success){
                                    viewModelScope.launch {
                                        dataBaseRepository.insertCategories(apiData)
                                        val syncItem = updateSyncGrid(CATEGORY)
                                        preferences.setCategorySyncGrid(syncItem.syncerGuid)
                                        updateSyncCount()
                                        println("SyncCategoryInsertionComplete ${syncDataState.value.syncCount}")
                                        apiData.result.items.forEach {category->
                                            println("menu api for : ${category.id}")
                                            categoryList.add(category)
                                        }
                                    }
                                }
                            },
                            onError = { errorMsg ->
                                updateLoginError(MENU_CATEGORY_ERROR_TITLE,errorMsg)
                            }
                        )
                    }
                }.await()
                println("categoryList :$categoryList")
                _syncMenu(categoryList)
            }
        }catch (e: Exception){
            val error="${e.message}"
            updateLoginError(MENU_CATEGORY_ERROR_TITLE,error)
        }
    }

    private suspend fun _syncMenu(items: List<CategoryItem>) {
        val newStock : MutableList<MenuItem> = mutableListOf()
        items.forEach { cat->
            println("MenuProductsAPI : ${cat.id}")
            updateSyncStatus("Syncing Menu For Category ID ${cat.id}")
            networkRepository.getMenuProducts(getBasicRequest(cat.id?:0)).collectLatest {response->
                observeResponseNew(response,
                    onLoading = {},
                    onSuccess = { menuData ->
                        if(menuData.success){
                            menuData.result.items.forEach { menu->
                                val updatedMenu=menu.copy(menuCategoryId=cat.id?:0, id = if(menu.id==0L) menu.productId else menu.id)
                                newStock.add(updatedMenu)
                            }
                            viewModelScope.launch {
                                val updatedStocks=newStock.map{menu->
                                    Stock(
                                        id = menu.id?:-1,
                                        name = menu.name?:"",
                                        imagePath = menu.imagePath ?: "",
                                        categoryId = menu.menuCategoryId?:-1,
                                        productId = menu.productId?:0,
                                        sortOrder = menu.sortOrder?:0,
                                        inventoryCode = menu.inventoryCode?:"",
                                        fgColor = menu.foreColor ?: "",
                                        bgColor = menu.backColor ?: "",
                                        barcode = menu.barCode?:""
                                    )
                                }
                                dataBaseRepository.insertNewStock(updatedStocks)
                                val syncItem = updateSyncGrid(MENU)
                                preferences.setStockSyncGrid(syncItem.syncerGuid)
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
            updateSyncStatus("Syncing Promotions...")
            networkRepository.getPromotions(PromotionRequest(tenantId = getTenantId())).collectLatest { apiResponse->
                observeResponseNew(apiResponse,
                    onLoading = {},
                    onSuccess = { apiData ->
                        if(apiData.success){
                            viewModelScope.launch {
                                apiData.result?.map {item->
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
                                dataBaseRepository.insertPromotions(apiData)
                                val syncItem = updateSyncGrid(PROMOTION)
                                preferences.setPromotionsSyncGrid(syncItem.syncerGuid)
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
                                preferences.setPaymentTypeSyncGrid(syncItem.syncerGuid)
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

    private suspend fun _loadNextSalesInvoiceNumber(){
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

    fun updateSyncCompleteStatus(value:Boolean) {
        _syncDataState.update { it.copy(syncComplete = value) }
    }
    
    // Handle any sync errors
    private fun handleError(errorTitle: String, errorMsg: String) {
        _syncDataState.update { it.copy(syncError = true, syncErrorInfo = "$errorTitle \n $errorMsg") }
    }

    private fun updateError(syncError:Boolean=true,errorTitle: String, errorMsg: String) {
        _syncDataState.update { it.copy(syncError = syncError,syncErrorInfo = "$errorTitle \n $errorMsg") }
    }

    // Cancel sync job when ViewModel is cleared
    fun stopPeriodicSync() {
        timerJob?.cancel()
        timerJob = null
        println("stopPeriodicSync")
    }

    override fun onCleared() {
        super.onCleared()
        //println("Cleared SyncViewModel")
        stopPeriodicSync()
    }
}