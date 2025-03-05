package com.hashmato.retailtouch.presentation.viewModels

import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hashmato.retailtouch.domain.model.login.AuthenticateDao
import com.hashmato.retailtouch.domain.ApiUtils.observeResponse
import com.hashmato.retailtouch.domain.ApiUtils.observeResponseNew
import com.hashmato.retailtouch.domain.PreferencesRepository
import com.hashmato.retailtouch.domain.RequestState
import com.hashmato.retailtouch.domain.SqlRepository
import com.hashmato.retailtouch.domain.model.ApiLoaderStateResponse
import com.hashmato.retailtouch.domain.model.AppState
import com.hashmato.retailtouch.domain.model.basic.BasicApiRequest
import com.hashmato.retailtouch.domain.model.employee.POSEmployee
import com.hashmato.retailtouch.domain.model.products.Stock
import com.hashmato.retailtouch.domain.model.login.LoginRequest
import com.hashmato.retailtouch.domain.model.login.LoginUiState
import com.hashmato.retailtouch.domain.model.menu.CategoryItem
import com.hashmato.retailtouch.domain.model.menu.CategoryResponse
import com.hashmato.retailtouch.domain.model.menu.MenuItem
import com.hashmato.retailtouch.domain.model.printer.GetPrintTemplateRequest
import com.hashmato.retailtouch.domain.model.printer.PrinterTemplates
import com.hashmato.retailtouch.domain.model.invoiceSaleTransactions.POSInvoiceRequest
import com.hashmato.retailtouch.domain.model.invoiceSaleTransactions.GetPosInvoiceResult
import com.hashmato.retailtouch.domain.model.location.Location
import com.hashmato.retailtouch.domain.model.login.RTLoginUser
import com.hashmato.retailtouch.domain.model.posInvoices.PendingSale
import com.hashmato.retailtouch.domain.model.posInvoices.PendingSaleDao
import com.hashmato.retailtouch.domain.model.productBarCode.ProductBarCodeResponse
import com.hashmato.retailtouch.domain.model.productLocations.ProductLocationResponse
import com.hashmato.retailtouch.domain.model.products.CreatePOSInvoiceRequest
import com.hashmato.retailtouch.domain.model.products.PosInvoice
import com.hashmato.retailtouch.domain.model.products.ProductWithTaxByLocationResponse
import com.hashmato.retailtouch.domain.model.promotions.PromotionRequest
import com.hashmato.retailtouch.domain.model.promotions.GetPromotionResult
import com.hashmato.retailtouch.domain.model.promotions.GetPromotionsByPriceResult
import com.hashmato.retailtouch.domain.model.promotions.GetPromotionsByQtyResult
import com.hashmato.retailtouch.domain.model.promotions.PromotionDetails
import com.hashmato.retailtouch.domain.model.promotions.PromotionItem
import com.hashmato.retailtouch.domain.model.sync.SyncItem
import com.hashmato.retailtouch.domain.model.sync.UnSyncList
import com.hashmato.retailtouch.domain.repositories.DataBaseRepository
import com.hashmato.retailtouch.domain.repositories.NetworkRepository
import com.hashmato.retailtouch.sync.SyncDataState
import com.hashmato.retailtouch.utils.AppConstants.EMPLOYEE_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppConstants.EMPLOYEE_ROLE_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppConstants.INVENTORY_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppConstants.INVOICE
import com.hashmato.retailtouch.utils.AppConstants.LARGE_PHONE_MAX_WIDTH
import com.hashmato.retailtouch.utils.AppConstants.LOCATION_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppConstants.MEMBER
import com.hashmato.retailtouch.utils.AppConstants.MEMBER_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppConstants.MEMBER_GROUP
import com.hashmato.retailtouch.utils.AppConstants.MENU_CATEGORY_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppConstants.MENU_PRODUCTS_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppConstants.PAYMENT_TYPE_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppConstants.PROMOTIONS_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppConstants.SMALL_PHONE_MAX_WIDTH
import com.hashmato.retailtouch.utils.AppConstants.SMALL_TABLET_MAX_WIDTH
import com.hashmato.retailtouch.utils.AppConstants.SYNC_SALES_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppConstants.SYNC_TEMPLATE_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppLanguage
import com.hashmato.retailtouch.utils.DateTimeUtils.getCurrentDateAndTimeInEpochMilliSeconds
import com.hashmato.retailtouch.utils.DateTimeUtils.getHoursDifferenceFromEpochMillSeconds
import com.hashmato.retailtouch.utils.DeviceType
import com.hashmato.retailtouch.utils.PrefKeys.SYNC_EXPIRY_THRESHOLD
import com.hashmato.retailtouch.utils.PrefKeys.TOKEN_EXPIRY_THRESHOLD
import com.hashmato.retailtouch.utils.TemplateType
import com.hashmato.retailtouch.utils.serializers.db.parsePriceBreakPromotionAttributes
import com.hashmato.retailtouch.utils.serializers.db.toDefaultLocation
import com.hashmato.retailtouch.utils.serializers.db.toJson
import com.hashmato.retailtouch.utils.serializers.db.toPOSEmployee
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class BaseViewModel: ViewModel(), KoinComponent {

    val networkRepository: NetworkRepository by inject()
    val preferences: PreferencesRepository by inject()
    val dataBaseRepository: DataBaseRepository by inject()
    val sqlRepository: SqlRepository by inject()
   //Changed
    private val _composeAppState = MutableStateFlow(AppState())
    val composeAppState: StateFlow<AppState> = _composeAppState.asStateFlow()

    private val _posSaleApiState: MutableStateFlow<ApiLoaderStateResponse> = MutableStateFlow(ApiLoaderStateResponse.Loader)
    val posSaleApiState: StateFlow<ApiLoaderStateResponse> = _posSaleApiState.asStateFlow()

    val _loginScreenState = MutableStateFlow(LoginUiState())
    val loginScreenState: StateFlow<LoginUiState> = _loginScreenState

    val _logoutFromServer = MutableStateFlow(false)
    val logoutFromServer: StateFlow<Boolean> = _logoutFromServer

    private val _syncDataState = MutableStateFlow(SyncDataState())
    val syncDataState: StateFlow<SyncDataState> = _syncDataState.asStateFlow()

    var syncingCount =1
    var insertionCount =1

    //For Rights
    val employeeDoa = MutableStateFlow<POSEmployee?>(null)

    private val _printerTemplates = MutableStateFlow<List<PrinterTemplates>?>(emptyList())
    val printerTemplates : StateFlow<List<PrinterTemplates>?> = _printerTemplates.asStateFlow()

    private val _syncInProgress = MutableStateFlow(false)
    val syncInProgress: StateFlow<Boolean> = _syncInProgress.asStateFlow()

    private val _uiUpdateStatus = MutableStateFlow(false)
    val uiUpdateStatus: StateFlow<Boolean> = _uiUpdateStatus.asStateFlow()

    // Expose the login state as a Flow<Boolean?>, with null indicating loading
    val isUserLoggedIn: StateFlow<Boolean?> = preferences.getUserLoggedIn()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = null
        )

    val currencySymbol: StateFlow<String> = preferences.getCurrencySymbol()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = ""
        )

    val authUser: StateFlow<AuthenticateDao?> = dataBaseRepository.getAuthUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = null
        )

    val RTUser: StateFlow<RTLoginUser?> = dataBaseRepository.getRTLoginUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = null
        )

    val isPrinterEnable: StateFlow<Boolean?> = preferences.getIsPrinterEnabled()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = null
        )

    val employee: StateFlow<POSEmployee?> = flow {
        // This flow emits the employee data asynchronously
        val employeeCode = getEmpCode()  // Suspends here
        emit(dataBaseRepository.getEmployee(employeeCode).firstOrNull())
    }.stateIn(
            scope = viewModelScope,  // Use viewModelScope
            started = SharingStarted.WhileSubscribed(5000),  // Keeps the flow alive for 5 seconds after subscription
            initialValue = null  // Initial state of the flow

    )

    val location: StateFlow<Location?> = flow {
       emit( dataBaseRepository.getSelectedLocation().firstOrNull())
    }.stateIn(
        scope = viewModelScope,  // Use viewModelScope
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null  // Initial state of the flow

    )


    suspend fun isDiscountEnabledTaxInclusiveName():Boolean{
        var enabled = false
       dataBaseRepository.getEmpRights().collectLatest { employee->
           employee.map { item->
               if(item.isAdmin || item.grantedPermissionNames?.contains("ITEMDISC") == true){
                   enabled=true
               }
           }
       }
        return enabled
    }

    fun updateScreenMode(width: Dp,height:Dp){
        val deviceType = when {
            width < SMALL_PHONE_MAX_WIDTH -> DeviceType.SMALL_PHONE
            width in SMALL_PHONE_MAX_WIDTH..LARGE_PHONE_MAX_WIDTH -> DeviceType.LARGE_PHONE
            width in LARGE_PHONE_MAX_WIDTH..SMALL_TABLET_MAX_WIDTH -> DeviceType.SMALL_TABLET
            else -> DeviceType.LARGE_TABLET
        }
        // Determine the orientation based on maxWidth and maxHeight
        _composeAppState.update { state -> state.copy(
            isTablet = deviceType == DeviceType.SMALL_TABLET || deviceType == DeviceType.LARGE_TABLET,
            screenWidth = width,
            deviceType = deviceType,
            isPortrait = height > width,
            isLandScape = width>height
        )
        }
    }


   /* private fun updateSyncProgress(syncStatus: Boolean) {
        _syncInProgress.update { syncStatus }
    }*/

    private fun updateUIStatus(syncStatus: Boolean) {
        _uiUpdateStatus.update { syncStatus }
    }


    suspend fun isCallCompleteSync() : Boolean{
        val lastSyncTs : Long = getLastSyncTs()
        val currentTime = getCurrentDateAndTimeInEpochMilliSeconds()
        val hoursPassed = getHoursDifferenceFromEpochMillSeconds(lastSyncTs, currentTime)
        println("HourPassed:$hoursPassed")
        return hoursPassed > SYNC_EXPIRY_THRESHOLD
    }


    /* ----------Sync On Login Apis-----------*/

    fun reSyncOnLogin(location: String) {
        viewModelScope.launch(Dispatchers.IO) {
            async { syncLocation(location) }.await()
            async { syncEmployees() }.await()
            async { syncEmployeeRole() }.await()
            async { syncMembers() }.await()
            async { syncMemberGroup() }.await()
            val categoriesList = async { syncCategories() }.await()
            async { syncMenus(categoriesList) }.await()
            val stockQtyMap=async { syncInventoryQuantity() }.await()
            async { syncInventory(stockQtyMap) }.await()
            async { syncInventoryBarcode() }.await()
            async { syncPromotion() }.await()
            async {syncLatestSales(skipCount= 0 ,maxResultCount=1)}.await()
            async {syncInvoiceReceiptTemplate(TemplateType.POSInvoice)}.await()
            async {syncInvoiceReceiptTemplate(TemplateType.PosSettlement)}.await()
            async {syncPaymentTypes()}.await()

            withContext(Dispatchers.Main){
                if(!_loginScreenState.value.isLoginError){
                    println("All Sync Operations Completed Successfully")
                    updateLoginSyncStatus("All Sync Operations have been Completed Successfully")
                    setLastSyncTs()
                    moveToNextScreen()
                }
            }
        }
    }

    private suspend fun syncLocation(location: String) {
        try {
            println("Syncing Location ${syncingCount++}")
            updateLoginSyncStatus("Syncing Location...")
            networkRepository.getLocationForUser(getBasicRequest()).collect{apiResponse->
                observeResponseNew(apiResponse,
                    onLoading = {
                        updateLoginSyncStatus("Syncing Location...")
                    },
                    onSuccess = { apiData ->
                        var userLocation = Location(name = "Empty Location", locationId = 0)
                        if(apiData.success && apiData.result.items.isNotEmpty()){
                            val locationsData = apiData.result.items
                            val locationItem = locationsData.first()
                            userLocation= Location(locationId = locationItem.id?:0, name = locationItem.name?:"", code = locationItem.code?:"", address1 = locationItem.address1?:"", address2 = locationItem.address2?:"")
                            if(location.isNotEmpty()){
                                val rtLocation = locationsData.find{ it.code?.lowercase()== location.lowercase() || it.name?.lowercase() == location.lowercase()}
                                if(rtLocation!=null){
                                    userLocation = Location(locationId = rtLocation.id?:0, name = rtLocation.name?:"", code = rtLocation.code?:"", address1 = rtLocation.address1?:"", address2 = rtLocation.address2?:"")
                                }
                            }
                        }
                        viewModelScope.launch {
                            setDefaultLocation(userLocation)
                            setDefaultLocationId(userLocation.locationId.toInt())
                            dataBaseRepository.insertLocation(apiData)
                            println("Insertion Location ${insertionCount++}")
                        }
                    },
                    onError = { errorMsg ->
                        updateLoginError(LOCATION_ERROR_TITLE,errorMsg)
                    }
                )
            }
        }
        catch (e:Exception){
            updateLoginError(LOCATION_ERROR_TITLE,e.message.toString())
        }
    }

    private suspend fun syncEmployees(){
        try {
            println("Syncing Employees : ${syncingCount++}")
            updateLoginSyncStatus("Syncing Employees...")
            networkRepository.getEmployees(getBasicRequest()).collect{apiResponse->
                observeResponseNew(apiResponse,
                    onLoading = {
                        updateLoginSyncStatus("Syncing Employees...")
                    },
                    onSuccess = { apiData ->
                        viewModelScope.launch {
                            dataBaseRepository.insertEmployees(apiData)
                            println("Insertion Employees ${insertionCount++}")
                        }
                    },
                    onError = {errorMsg->
                        updateLoginError(EMPLOYEE_ERROR_TITLE,errorMsg)
                    }
                )
            }
        }catch (e: Exception){
            val error=e.message.toString()
            updateLoginError(EMPLOYEE_ERROR_TITLE,error)
        }
    }

    private suspend fun syncEmployeeRole(){
        try {
            updateLoginSyncStatus("Syncing Employees Role...")
            println("Syncing Employees Role : ${syncingCount++}")
            networkRepository.getEmployeeRole(getBasicRequest()).collect{apiResponse->
                observeResponseNew(apiResponse,
                    onLoading = {  updateLoginSyncStatus("Syncing Employees Role...")},
                    onSuccess = { apiData ->
                        viewModelScope.launch {
                            dataBaseRepository.insertEmpRole(apiData)
                            println("Insertion Employee Role ${insertionCount++}")
                        } },
                    onError = {errorMsg->
                        updateLoginError(EMPLOYEE_ROLE_ERROR_TITLE,errorMsg)
                    }
                )
            }
        }catch (e: Exception){
            val error="${e.message}"
            updateLoginError(EMPLOYEE_ROLE_ERROR_TITLE,error)
        }
    }

     suspend fun syncMembers(){
        try {
            updateLoginSyncStatus("Syncing Members...")
            println("Syncing Members : ${syncingCount++}")
            networkRepository.getMembers(getBasicTenantRequest()).collectLatest {apiResponse->
                observeResponseNew(apiResponse,
                    onLoading = { updateLoginSyncStatus("Syncing Members...") },
                    onSuccess = { apiData ->
                        if(apiData.success){
                            viewModelScope.launch {
                                dataBaseRepository.insertMembers(apiData)
                                val syncItem = updateSyncGrid(MEMBER)
                                preferences.setMemberSyncGrid(syncItem.syncerGuid)
                                updateSyncCount()
                                println("SyncMemberInsertionComplete ${syncDataState.value.syncCount}")
                                //println("Insertion Members ${insertionCount++}")
                            }
                        }
                    },
                    onError = {
                            errorMsg ->
                        updateLoginError(MEMBER_ERROR_TITLE,errorMsg)
                    }
                )
            }
        }catch (e: Exception){
            val error="${e.message}"
            updateLoginError(MEMBER_ERROR_TITLE,error)
        }
    }

     suspend fun syncMemberGroup(){
        try {
            updateLoginSyncStatus("Syncing Member Group")
            println("MemberGroup API CALL : ${syncingCount++}")
            networkRepository.getMemberGroup(getBasicTenantRequest()).collectLatest {apiResponse->
                observeResponseNew(apiResponse,
                    onLoading = {
                        updateLoginSyncStatus("Syncing Member Group")
                    },
                    onSuccess = { apiData ->
                        if(apiData.success){
                            viewModelScope.launch {
                                dataBaseRepository.insertMemberGroup(apiData)
                                val syncItem = updateSyncGrid(MEMBER_GROUP)
                                preferences.setMemberGroupSyncGrid(syncItem.syncerGuid)
                                updateSyncCount()
                                println("SyncMemberGroupInsertionComplete ${syncDataState.value.syncCount}")
                                //println("Insertion MemberGroup : ${insertionCount++}")
                            }
                        }
                    },
                    onError = {
                            errorMsg ->
                        updateLoginError(MEMBER_ERROR_TITLE,errorMsg)
                    }
                )
            }
        }catch (e: Exception){
            val error="${e.message}"
            updateLoginError(MEMBER_ERROR_TITLE,error)
        }
    }

    suspend fun syncCategories():List<CategoryItem> = withContext(Dispatchers.IO) {

        var categoryItems: List<CategoryItem> = listOf()
        try {
            updateLoginSyncStatus("Syncing Product Category...")
            println("Syncing Product Category : ${syncingCount++}")
            networkRepository.getMenuCategories(getBasicRequest()).collect {apiResponse->
                observeResponseNew(apiResponse,
                    onLoading = {  },
                    onSuccess = { apiData ->
                        if(apiData.success){
                            viewModelScope.launch {
                                dataBaseRepository.insertCategories(apiData)
                                println("Insertion Product Category : ${insertionCount++}")
                            }
                            categoryItems = apiData.result.items
                        }
                    },
                    onError = { errorMsg ->
                        updateLoginError(MENU_CATEGORY_ERROR_TITLE,errorMsg)
                    }
                )
            }
        }catch (e: Exception){
            val errorM="${e.message}"
            updateLoginError(MENU_CATEGORY_ERROR_TITLE,errorM)
        }
        // Return the collected list once the flow completes
        println("categoryList :$categoryItems")
        return@withContext categoryItems
    }

    private suspend fun syncMenus(categoryList:List<CategoryItem>){
        try {
            val newStock : MutableList<MenuItem> = mutableListOf()
            categoryList.forEach { cat->
                updateLoginSyncStatus("Syncing Product Menu for ..${cat.id}")
                println("Syncing Product Menu : ${syncingCount++}")
                networkRepository.getMenuProducts(getBasicRequest(cat.id?:0)).collect {response->
                    observeResponseNew(response,
                        onLoading = {
                            updateLoginSyncStatus("Syncing Product Menu for ..${cat.id}")
                        },
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
                                    println("Insertion Product Menu : ${insertionCount++}")
                                }
                            }
                        },
                        onError = {
                                errorMsg ->
                            updateLoginError(MENU_PRODUCTS_ERROR_TITLE,errorMsg)
                        }
                    )
                }
            }
        }catch (e: Exception){
            val errorM="${e.message}"
            updateLoginError(MENU_PRODUCTS_ERROR_TITLE,errorM)
        }
    }

    private suspend fun syncInventoryQuantity(lastSyncTime: String?=null):MutableMap<Int, Double> = withContext(Dispatchers.IO) {
        val stockQtyMap: MutableMap<Int, Double> = mutableMapOf()
        try {
            updateLoginSyncStatus("Syncing Inventory Quantity..")
            println("Syncing Inventory Quantity : ${syncingCount++}")
            networkRepository.getProductLocation(getBasicRequest()).collect {stockAvailResponse->
                observeResponseNew(stockAvailResponse,
                    onLoading = {
                        updateLoginSyncStatus("Syncing Inventory Quantity..")
                    },
                    onSuccess = { apiData ->
                        if (apiData.success) {
                            apiData.result?.items?.forEach { stock ->
                                stockQtyMap[stock.productId]=stock.qtyOnHand?:0.0
                            }
                            viewModelScope.launch {
                                dataBaseRepository.insertUpdateProductQuantity(apiData,lastSyncTime)
                                println("Insertion Inventory Quantity : ${insertionCount++}")
                            }
                        }
                    },
                    onError = { errorMsg ->
                        updateLoginError(INVENTORY_ERROR_TITLE, errorMsg)
                    }
                )
            }
        }catch (e: Exception){
            val errorM="${e.message}"
            updateLoginError(INVENTORY_ERROR_TITLE,errorM)
        }
        // Return the collected list once the flow completes
        println("stockQtyMap :$stockQtyMap")
        return@withContext stockQtyMap
    }

    private suspend fun syncInventory(stockQtyMap:MutableMap<Int, Double>,lastSyncTime: String?=null){
        try {
            updateLoginSyncStatus("Syncing Stock Products..")
            println("Syncing Stock Products : ${syncingCount++}")
            networkRepository.getProductsWithTax(getBasicRequest()).collect{inventoryResponse->
                observeResponseNew(inventoryResponse,
                    onLoading = {
                        updateLoginSyncStatus("Syncing Stock Products..")
                    },
                    onSuccess = { apiData ->
                        if(apiData.success){
                            viewModelScope.launch {
                                dataBaseRepository.insertUpdateInventory(apiData,lastSyncTime, stockQtyMap)
                                println("Insertion Stock Products: ${insertionCount++}")
                            }
                        }
                    },
                    onError = {
                            errorMsg ->
                        updateLoginError(INVENTORY_ERROR_TITLE,errorMsg)
                    }
                )
            }
        }catch (e: Exception){
            val errorM="${e.message}"
            updateLoginError(INVENTORY_ERROR_TITLE,errorM)
        }
    }

    private suspend fun syncInventoryBarcode(lastSyncTime: String?=null){
        try {
            updateLoginSyncStatus("Syncing Product Barcode...")
            println("Syncing Product Barcode : ${syncingCount++}")
            networkRepository.getProductBarCode(getBasicRequest()).collect{inventoryResponse->
                observeResponseNew(inventoryResponse,
                    onLoading = {
                        updateLoginSyncStatus("Syncing Product Barcode...")
                    },
                    onSuccess = { apiData ->
                        viewModelScope.launch {
                            dataBaseRepository.insertUpdateBarcode(apiResponse = apiData,lastSyncDateTime=lastSyncTime)
                            println("Insertion Product Barcode: ${insertionCount++}")
                        }
                    },
                    onError = {
                            errorMsg ->
                        updateLoginError(INVENTORY_ERROR_TITLE,errorMsg)
                    }
                )
            }
        }catch (e: Exception){
            val errorM="${e.message}"
            updateLoginError(INVENTORY_ERROR_TITLE,errorM)
        }
    }

    private suspend fun syncPromotion(){
        try {
            val promotionList: MutableList<PromotionItem> = mutableListOf()
            updateLoginSyncStatus("Syncing Promotions...")
            println("Syncing Promotions : ${syncingCount++}")
            networkRepository.getPromotions(PromotionRequest(tenantId = getTenantId())).collect { apiResponse->
                observeResponseNew(apiResponse,
                    onLoading = {  },
                    onSuccess = { apiData ->
                        if(apiData.success){
                            viewModelScope.launch {
                                apiData.result?.forEach {promotion->
                                    promotionList.add(promotion)
                                    when(promotion.promotionTypeName){

                                        "PromotionByQty"->{
                                            updateLoginSyncStatus("Syncing Promotion By QTY....")
                                            networkRepository.getPromotionsByQty(PromotionRequest(id = promotion.id.toInt())).collect { promotionsData->
                                                observeResponseNew(promotionsData,
                                                    onLoading = { },
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
                                                        updateLoginError(PROMOTIONS_ERROR_TITLE,errorMsg)
                                                    }
                                                )
                                            }
                                        }

                                        "PromotionByPrice" ->{
                                            updateLoginSyncStatus("Syncing Promotion By Price....")
                                            networkRepository.getPromotionsByPrice(PromotionRequest(id = promotion.id.toInt())).collectLatest { promotionsData->
                                                observeResponseNew(promotionsData,
                                                    onLoading = { },
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
                                                        updateLoginError(PROMOTIONS_ERROR_TITLE,errorMsg)
                                                    }
                                                )
                                            }
                                        }

                                        "PromotionByPriceBreak"->{
                                            updateLoginSyncStatus("Syncing Promotion By Price Break....")
                                            networkRepository.getPromotionsByQty(PromotionRequest(id = promotion.id.toInt())).collectLatest { promotionsData->
                                                observeResponseNew(promotionsData,
                                                    onLoading = { },
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
                                                        updateLoginError(PROMOTIONS_ERROR_TITLE,errorMsg)
                                                    }
                                                )
                                            }
                                        }

                                        else->{
                                            updateLoginSyncStatus("Syncing Promotion Default....")
                                            networkRepository.getPromotionsByQty(PromotionRequest(id = promotion.id.toInt())).collectLatest { promotionsData->
                                                observeResponseNew(promotionsData,
                                                    onLoading = { },
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
                                                                println("Insertion Promotion Details : ${insertionCount++}")
                                                            }
                                                        }
                                                    },
                                                    onError = { errorMsg ->
                                                        updateLoginError(PROMOTIONS_ERROR_TITLE,errorMsg)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                                dataBaseRepository.insertPromotions(apiData)
                                println("Insertion Promotions : ${insertionCount++}")
                            }
                        }
                    },
                    onError = {
                            errorMsg ->
                        updateLoginError(PROMOTIONS_ERROR_TITLE,errorMsg)
                    }
                )
            }
        }catch (e: Exception){
            val error="${e.message}"
            updateLoginError(PROMOTIONS_ERROR_TITLE,error)
        }
    }

    suspend fun syncInvoiceReceiptTemplate(templateType: TemplateType){
        try {
            updateLoginSyncStatus("Syncing Invoice Template...")
            println("Syncing Invoice Template : ${syncingCount++}")
            networkRepository.getPrintTemplate(GetPrintTemplateRequest(locationId = getLocationId(), type = templateType.toValue())).collect {apiResponse->
                observeResponseNew(apiResponse,
                    onLoading = {  },
                    onSuccess = { apiData ->
                        if(apiData.success){
                            viewModelScope.launch {
                                dataBaseRepository.insertOrUpdateTemplate(apiData)
                                println("Insertion Invoice Template : ${insertionCount++}")
                            }
                        }
                    },
                    onError = { errorMsg ->
                        updateLoginError(SYNC_TEMPLATE_ERROR_TITLE,errorMsg)
                    }
                )
            }
        }
        catch (e: Exception){
            val error="${e.message}"
            updateLoginError(SYNC_TEMPLATE_ERROR_TITLE,error)
        }
    }

    private suspend fun syncPaymentTypes(){
        try {
            updateLoginSyncStatus("Syncing Payment Type...")
            println("Syncing Payment Type : ${syncingCount++}")
            networkRepository.getPaymentTypes(getBasicTenantRequest()).collect {apiResponse->
                observeResponseNew(apiResponse,
                    onLoading = {
                        updateLoginSyncStatus("Syncing Payment Type...")
                    },
                    onSuccess = { apiData ->
                        if(apiData.success){
                            viewModelScope.launch {
                                dataBaseRepository.insertPaymentType(apiData)
                                println("Insertion Payment Type : ${syncingCount++}")
                            }
                        }
                    },
                    onError = { errorMsg ->
                        updateLoginError(PAYMENT_TYPE_ERROR_TITLE,errorMsg)
                    }
                )
            }
        }catch (e: Exception){
            val error="${e.message}"
            updateLoginError(PAYMENT_TYPE_ERROR_TITLE,error)
        }
    }

    private suspend fun syncPOSSalesCount():Long = withContext(Dispatchers.IO) {
        var salesCount:Long= 1000
        try {
            updateLoginSyncStatus("Syncing POS Sales..")
            println("Syncing POS Sales : ${syncingCount++}")
            networkRepository.getLatestSales(POSInvoiceRequest(locationId = getLocationId(), maxResultCount=1, skipCount = 0, sorting = "Id")).collect {apiResponse->
                observeResponseNew(apiResponse,
                    onSuccess = { apiData ->
                        if (apiData.success) {
                            salesCount=apiData.result?.totalCount?:1000
                        }
                    },
                    onError = { errorMsg ->
                        updateLoginError(SYNC_SALES_ERROR_TITLE, errorMsg)
                    }
                )
            }
        }catch (e: Exception){
            val errorM="${e.message}"
            updateLoginError(SYNC_SALES_ERROR_TITLE,errorM)
        }
        // Return the collected list once the flow completes
        println("SalesCount :$salesCount")
        return@withContext salesCount
    }

    suspend fun syncLatestSales(skipCount: Long?, maxResultCount: Int?) {
        try {
            updateSyncStatus("Syncing POS Sales...")
            println("SyncingPOSSales. : ${syncDataState.value.syncCount}")
            networkRepository.getLatestSales(POSInvoiceRequest(
                locationId = getLocationId(),
                maxResultCount = maxResultCount ?: 1000,
                skipCount = skipCount?.toInt() ?: 0,
                sorting = "Id"
            )).collect {apiResponse->
                observeResponseNew(apiResponse,
                    onLoading = {  },
                    onSuccess = { apiData ->
                        if(apiData.success){
                            viewModelScope.launch {
                                val totalCount = apiData.result?.totalCount?:0
                                if (totalCount >= 1000) {
                                    syncLatestSales(skipCount= totalCount - 600,maxResultCount=1000)
                                } else {
                                    dataBaseRepository.insertNewLatestSales(apiData)
                                    updateSyncGrid(INVOICE)
                                    updateSyncCount()
                                    println("SyncInvoiceSalesInsertionComplete ${syncDataState.value.syncCount}")
                                }
                            }
                        }
                    },
                    onError = { errorMsg ->
                        updateError(errorTitle = SYNC_SALES_ERROR_TITLE, errorMsg = errorMsg)
                    }
                )
            }
        }catch (e: Exception){
            val errorM="${e.message}"
            updateError(errorTitle = SYNC_SALES_ERROR_TITLE, errorMsg = errorM)
        }

    }

    //Api Calls

    suspend fun createUpdatePosInvoice(posInvoice: PosInvoice, pendingSaleCount : Int, posSaleId: Long){
        networkRepository.createUpdatePosInvoice(CreatePOSInvoiceRequest(posInvoice = posInvoice)).collect { apiResponse->
            observeResponseNew(apiResponse,
                onLoading = {
                    _posSaleApiState.update { ApiLoaderStateResponse.Loader }
                },
                onSuccess = { apiData ->
                    if(apiData.success && apiData.result?.posInvoiceNo != null){
                        println("posInvoiceNo:${apiData.result.posInvoiceNo}")
                        viewModelScope.launch {
                            try {
                                dataBaseRepository.addUpdatePendingSales(
                                    PendingSaleDao(
                                        posInvoice = posInvoice,
                                        posSaleId = posSaleId,
                                        isDbUpdate = true,
                                        isSynced = true
                                    )
                                )
                                println("API call succeeded for posSaleId: $posSaleId")
                                _posSaleApiState.update { ApiLoaderStateResponse.Success }
                                // Cancel the loader only after the last successful API call
                                //var localPendingCount = pendingSaleCount
                                /*if (--localPendingCount==0) {
                                    _posSaleApiState.update { ApiLoaderStateResponse.Success }
                                    println("Last API call succeeded. Loader canceled.")
                                }*/
                            }catch (ex:Exception){
                                _posSaleApiState.update { ApiLoaderStateResponse.Error("Error saving invoice \n ${ex.message}") }
                            }
                        }
                    }
                },
                onError = { errorMsg ->
                    println(errorMsg)
                    _posSaleApiState.update { ApiLoaderStateResponse.Error("Error saving invoice \n $errorMsg") }
                }
            )
        }
    }

    fun deClassifyPendingRecord(data: PendingSale, location: Location, tenantId: Int): PosInvoice {
        val posInvoice= PosInvoice(
            id = 0,
            tenantId = tenantId,
            employeeId = data.employeeId,
            locationId = location.locationId,
            locationCode = location.code,
            terminalId = location.locationId,
            terminalName = data.terminalName,
            isRetailWebRequest=data.isRetailWebRequest,
            invoiceNo = data.invoiceNo,
            invoiceDate= data.invoiceDate,
            invoiceTotal = data.invoiceTotal, //before Tax
            invoiceItemDiscount = data.invoiceItemDiscount,
            invoiceTotalValue= data.invoiceTotalValue,
            invoiceNetDiscountPerc= data.invoiceNetDiscountPerc,
            invoiceNetDiscount= data.invoiceNetDiscount,
            invoiceTotalAmount=data.invoiceTotalAmount,
            invoiceSubTotal= data.invoiceSubTotal,
            invoiceTax= data.globalTax,
            invoiceRoundingAmount = data.invoiceRoundingAmount,
            invoiceNetTotal= data.invoiceNetTotal,
            invoiceNetCost= data.invoiceNetCost,
            paid= data.paid, //netCost
            memberId = data.memberId,
            posInvoiceDetails = data.posInvoiceDetailRecord?:emptyList(),
            posPayments = data.posPaymentConfigRecord?:emptyList(),
            qty = data.qty,
            customerName = data.memberName,
            address1 = data.address1 ,
            address2 = data.address2 ,
        )

        return posInvoice
    }

    fun updateSales(){
        try {
            updateSyncStatus("Syncing Sales History")
            viewModelScope.launch {
                val job = async { getSalesTotalCount() }
                val saleRecord = job.await()
                saleRecord.collectLatest { sale->
                    when(sale){
                        is RequestState.Error -> {}
                        is RequestState.Idle -> {}
                        is RequestState.Loading -> {}
                        is RequestState.Success -> {
                            val saleCount=sale.data.result?.totalCount?:1000
                            getLatestSale(skipCount = saleCount-5, maxResultCount = 10).collect {
                                observeLatestSales(it)
                            }
                        }
                    }
                }
            }
        }catch (e: Exception){
            updateSyncProgress(false)
        }

    }

    private suspend fun getSalesTotalCount(): Flow<RequestState<GetPosInvoiceResult>> {
        return networkRepository.getLatestSales(POSInvoiceRequest(locationId = getLocationId(), maxResultCount=1, skipCount = 0, sorting = "Id"))
    }

    private suspend fun getLatestSale(skipCount: Long?, maxResultCount:Int?): Flow<RequestState<GetPosInvoiceResult>> {
       return networkRepository.getLatestSales(
           POSInvoiceRequest(
               locationId = getLocationId(),
               maxResultCount=maxResultCount?:1000,
               skipCount = skipCount?.toInt()?:0,
               sorting = "Id")
       )
    }


    /*------Api Call End--------*/


    private fun observeLatestSales(apiResponse: RequestState<GetPosInvoiceResult>) {
        observeResponseNew(apiResponse,
            onLoading = {
                updateSyncProgress(true)
            },
            onSuccess = { apiData ->
                if(apiData.success){
                    viewModelScope.launch {
                        dataBaseRepository.insertNewLatestSales(apiData)
                        //updateSyncGrid(INVOICE)
                        //set
                        //updateLastSyncTs(getCurrentDateAndTimeInEpochMilliSeconds())
                        updateSyncProgress(false)
                        //updateSyncStatus("")
                        //handleError(false,"","")
                        updateUIStatus(true)
                    }
                }
            },
            onError = {
                    errorMsg ->
                //handleError(true,SYNC_SALES_ERROR_TITLE,errorMsg)
                updateSyncProgress(false)
                updateUIStatus(true)
            }
        )

    }
    fun updateLoginState(loading: Boolean, successfulLogin:Boolean, loginError: Boolean, title :String,error:String) {
        viewModelScope.launch {
            _loginScreenState.update {
                it.copy(
                    isLoading = loading,
                    isSuccessfulLogin = successfulLogin,
                    isLoginError = loginError,
                    loginErrorMessage = error,
                    loginErrorTitle = title,
                )
            }
        }
    }

    fun updateLoginSyncStatus(loadingMsg:String) {
        viewModelScope.launch {
            _loginScreenState.value = _loginScreenState.value.copy(
                loadingMessage = loadingMsg
            )
        }
    }

    private fun moveToNextScreen(){
        setUserLoggedIn(true)
        updateLoginState(
            loading = false,
            loginError = false,
            successfulLogin = true,
            error = "",
            title = ""
        )

    }

     fun updateLoginError(errorTitle:String, errorMsg: String) {
        viewModelScope.launch(Dispatchers.Main) {
            println(errorMsg)
            updateLoginState(
                loading = _loginScreenState.value.isLoading,
                successfulLogin = false,
                loginError = true,
                title = errorTitle,
                error = errorMsg)
        }
    }

    fun updateSyncGrid(name: String): SyncItem {
        val state=syncDataState.value
        return state.syncerGuid.items.firstOrNull{ it.name.uppercase() == name }
            ?: SyncItem(name = name, syncerGuid = "", id = 0)
    }


    fun updateSyncerGuid(mUnSyncList: UnSyncList) {
        _syncDataState.update { it.copy(syncerGuid = mUnSyncList) }
    }

    fun updateInvoiceSyncing(syncStatus: Boolean) {
        _syncDataState.update { it.copy(syncingPosInvoices = syncStatus) }
    }

    fun updateSyncProgress(syncStatus: Boolean) {
        _syncDataState.update { it.copy(syncInProgress = syncStatus) }
    }

    fun updateSyncStatus(syncStatus: String) {
        _syncDataState.update { it.copy(syncProgressStatus = syncStatus) }
    }

    // Increment the sync count
    fun updateSyncCount() {
        _syncDataState.update { it.copy(syncCount = it.syncCount + 1) }
    }
    fun updateSyncCountZero() {
        _syncDataState.update { it.copy(syncCount = 0) }
    }

    fun updateSyncCompleteStatus(value:Boolean) {
        _syncDataState.update { it.copy(syncComplete = value) }
    }

    // Handle any sync errors
    fun handleError(errorTitle: String, errorMsg: String) {
        _syncDataState.update { it.copy(syncError = true, syncErrorInfo = "$errorTitle \n $errorMsg") }
    }

    fun updateError(syncError:Boolean=true,errorTitle: String, errorMsg: String) {
        _syncDataState.update { it.copy(syncError = syncError,syncErrorInfo = "$errorTitle \n $errorMsg") }
    }

    suspend fun getCurrencySymbol() : String{
        return preferences.getCurrencySymbol().first()
    }


    suspend fun setEmployeeCode(code:String){
        preferences.setEmployeeCode(code)
    }

    suspend fun getEmpCode() :String{
        return preferences.getEmployeeCode().first()
    }

    private fun setUserLoggedIn(result:Boolean){
        viewModelScope.launch {
            preferences.setUserLoggedIn(result)
        }
    }

    suspend fun getBasicRequest() = BasicApiRequest(
        tenantId = preferences.getTenantId().first(),
        locationId = preferences.getLocationId().first(),
       // lastSyncDateTime = _lastSyncDateTime.value
        )

    fun getBasicRequest(id:Int) = BasicApiRequest(
        id =id
    )

     suspend fun getBasicTenantRequest() = BasicApiRequest(
        tenantId = preferences.getTenantId().first()
    )


    //suspend fun getLocationId() = dataBaseRepository.getSelectedLocation().first()?.locationId?.toInt()
    suspend fun getTenantId() = preferences.getTenantId().first()
    suspend fun getLocation() = dataBaseRepository.getSelectedLocation().first()

    suspend fun getUserId() :Long{
        return preferences.getUserId().first()
    }

    suspend fun getLastTokenTime() : Long{
        return preferences.getTokenTime().first()
    }


    suspend fun setDefaultLocation(location: Location){
        preferences.setLocation(location.toJson())
    }

    suspend fun getDefaultLocation():Location{
        return preferences.getLocation().first().toDefaultLocation()
    }

    suspend fun setDefaultLocationId(locationId:Int){
        preferences.setLocationId(locationId)
    }
    suspend fun getLocationName():String{
        return preferences.getLocation().first().toDefaultLocation().name
    }

    suspend fun getLocationId():Int{
        return preferences.getLocation().first().toDefaultLocation().locationId.toInt()
    }

    suspend fun getMemberSyncGrid() : String{
        return preferences.getMemberSyncGrid().first()
    }

    suspend fun getMemberGroupSyncGrid() : String{
        return preferences.getMemberGroupSyncGrid().first()
    }

    suspend fun getProductsSyncGrid() : String{
        return preferences.getProductsSyncGrid().first()
    }

    suspend fun getCategorySyncGrid() : String{
        return preferences.getCategorySyncGrid().first()
    }

    suspend fun getStockSyncGrid() : String{
        return preferences.getStockSyncGrid().first()
    }

    suspend fun getPromotionsSyncGrid() : String{
        return preferences.getPromotionsSyncGrid().first()
    }

    suspend fun getTemplateSyncGrid() : String{
        return preferences.getTemplateSyncGrid().first()
    }

    suspend fun getPaymentTypeSyncGrid() : String{
        return preferences.getPaymentTypeSyncGrid().first()
    }

    suspend fun setLastSyncTs(){
        preferences.setLastSyncTs(getCurrentDateAndTimeInEpochMilliSeconds())
    }

    suspend fun getLastSyncTs():Long{
      return  preferences.getLastSyncTs().first()
    }

    suspend fun setPOSEmployee(employee:POSEmployee){
        preferences.setPOSEmployee(employee.toJson())
    }

    suspend fun getPOSEmployee():POSEmployee{
        return preferences.getPOSEmployee().first().toPOSEmployee()
    }

    suspend fun updatePOSEmployees(employee: POSEmployee
    ) {
        withContext(Dispatchers.IO) {
            val mPOSEmployee = POSEmployee(
                employeeId = employee.employeeId,
                employeeName = employee.employeeName,
                employeeCode = employee.employeeCode,
                employeeRoleName = employee.employeeRoleName,
                employeePassword = employee.employeePassword,
                employeeCategoryName = employee.employeeCategoryName,
                employeeDepartmentName = employee.employeeDepartmentName,
                isAdmin = employee.isAdmin,
                isDeleted = employee.isDeleted,
                isPosEmployee = true,
            )
            sqlRepository.updatePOSEmployee(mPOSEmployee)
        }
    }

    suspend fun getPosEmployees(): List<POSEmployee>{
        val allPosEmployees = sqlRepository.getPOSEmployees().first() // Collects the list.
        val currentEmployee = getPOSEmployee()
        return allPosEmployees.map {employee->
             POSEmployee(
                employeeId = employee.employeeId,
                employeeName = employee.employeeName,
                employeeCode = employee.employeeCode,
                employeeRoleName = employee.employeeRoleName,
                employeePassword = employee.employeePassword,
                employeeCategoryName = employee.employeeCategoryName,
                employeeDepartmentName = employee.employeeDepartmentName,
                isAdmin = employee.isAdmin,
                isDeleted = employee.isDeleted,
                isPosEmployee = currentEmployee.employeeId==employee.employeeId,
            )
        }
    }

    suspend fun getInventoryUniqueCount():Int{
       return sqlRepository.getProductCount().first()
    }

    suspend fun getCategoriesCount(): Int {
        return sqlRepository.getMenuCategoriesCount().first()
    }

    suspend fun getMenuItemsCount(): Int {
        return sqlRepository.getMenuItemsCount().first()
    }

    suspend fun getBarcodesCount(): Int {
        return sqlRepository.getBarcodeCount().first()
    }

    suspend fun getPendingSaleCount(): Long {
        return sqlRepository.getAllPendingSalesCount().first()
    }

    // Load from preferencesRepository

    suspend fun setToken(token: String) {
        preferences.setToken(token)
    }

    suspend fun setReSyncTimer(time:Int) {
        preferences.setReSyncTimer(time)
    }

    suspend fun getReSyncTimer(): Int {
        return preferences.getReSyncTime().first()
    }


    suspend fun getCurrentServer() : String{
        return preferences.getBaseURL().first()
    }

    suspend fun setNetworkConfig(networkConfig:String){
        preferences.setNetworkConfig(networkConfig)
    }

    suspend fun getNetworkConfig():String{
        return preferences.getNetworkConfig().first()
    }

    suspend fun setGridViewOptions(updatedValue:Int){
        preferences.setGridViewOptions(updatedValue)
    }

    suspend fun getGridViewCount() : Int{
        return preferences.getGridViewOptions().first()
    }

    suspend fun setRoundOffOption(updatedValue:Int){
        preferences.setRoundOffOption(updatedValue)
    }

    suspend fun getRoundOffOption() : Int{
        return preferences.getRoundOffOption().first()
    }

    suspend fun setMergeCartItems(updatedValue:Boolean){
        preferences.setMergeCartItems(updatedValue)
    }

    suspend fun getMergeCartItems() : Boolean{
        return preferences.getMergeCartItems().first()
    }

    suspend fun setFastPaymentMode(updatedValue:Boolean) {
        preferences.setFastPaymentMode(updatedValue)
    }

    suspend fun getFastPaymentMode():Boolean {
        return preferences.getFastPaymentMode().first()
    }

    suspend fun setPaymentConfirmPopup(updatedValue:Boolean) {
        preferences.setPaymentConfirmPopup(updatedValue)
    }

    suspend fun getPaymentConfirmPopup():Boolean {
        return preferences.getPaymentConfirmPopup().first()
    }

    suspend fun getIsMergeCartItem(): Boolean {
        return preferences.getMergeCartItems().first()
    }

    fun observeNetworkConfig(): Flow<String> {
        return preferences.getNetworkConfig()
    }

    suspend fun changeAppLanguage(value: AppLanguage) {
      preferences.setLanguage(value.name)
    }

    suspend fun getAppLanguage():String {
       return preferences.getLanguage().first()
    }

    fun logoutFromThisServer() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Start all three operations concurrently
                val jobs = listOf(
                    async { resetStates() },
                    async { emptyDataBase() },
                    async { emptyLocalPref() }
                )
                // Wait for all jobs to complete
                jobs.awaitAll()
                // After all operations complete, update logout status
                updateLogout(true)
            } catch (e: Exception) {
                // Handle exceptions if needed (e.g., logging or user feedback)
                e.printStackTrace()
            }
        }
    }

    private fun updateLogout(logoutValue: Boolean) {
        viewModelScope.launch {
            _logoutFromServer.update { logoutValue }
        }
    }

    private fun resetStates() {
        viewModelScope.launch {
            val jobs = listOf(
                async { employeeDoa.update { null } }
            )
            jobs.awaitAll()
        }
    }

    private fun emptyLocalPref(){
        viewModelScope.launch {
            val jobs = listOf(
                async { preferences.setBaseURL("") },
                async { preferences.setToken("") },
                async { preferences.setUserName("") },
                async { preferences.setUserPass("") },
                async { preferences.setTenancyName("") },
                async { preferences.setUserId(-1) },
                async { preferences.setTenantId(-1) },
                async { preferences.setLocationId(-1) },
                async { preferences.setLocation("") },
                async { preferences.setUserLoggedIn(false) },
                async { preferences.setLastSyncTs(-1) },
                async { preferences.setCategorySyncGrid("") },
                async { preferences.setMemberSyncGrid("") },
                async { preferences.setMemberGroupSyncGrid("") },
                async { preferences.setProductsSyncGrid("") },
                async { preferences.setPromotionsSyncGrid("") },
                async { preferences.setTemplateSyncGrid("") },
                async { preferences.setPaymentTypeSyncGrid("") },
                async { preferences.setCurrencySymbol("") },
                async { preferences.setEmployeeCode("") },
                async { preferences.setNetworkConfig("") },
                async { preferences.setLanguage("") },
                async { preferences.setNextSalesInvoiceNumber("") },
                async { preferences.setSalesInvoicePrefix("") },
                async { preferences.setSalesInvoiceNoLength(0)},
                async { preferences.setIsPrinterEnabled(false) },
                async { preferences.setMergeCartItems(true) },
                async { preferences.setFastPaymentMode(false) },
                async { preferences.setOverlayState(false) },
                async { preferences.setGridViewOptions(3) },
                async { preferences.setTerminalCode("") },
            )
            jobs.awaitAll()
        }
    }

    fun emptyDataBase() {
        viewModelScope.launch {
            val jobs = listOf(
                async { dataBaseRepository.clearAuthentication() },
                async { dataBaseRepository.clearExistingLocations() },
                async { dataBaseRepository.clearEmployees() },
                async { dataBaseRepository.clearEmployeeRole() },
                async { dataBaseRepository.clearEmployeeRights() },
                async { dataBaseRepository.clearMember()},
                async { dataBaseRepository.clearMemberGroup() },
                async { dataBaseRepository.clearStocksQty() },
                async { dataBaseRepository.clearStocks() },
                async { dataBaseRepository.clearBarcode() },
                async { dataBaseRepository.clearCategory() },
                async { dataBaseRepository.clearMenuItems() },
                async { dataBaseRepository.clearScannedProduct() },
                async { dataBaseRepository.clearPOSHoldSales() },
                async { dataBaseRepository.clearPromotion() },
                async { dataBaseRepository.clearPromotionDetails() },
                async { dataBaseRepository.clearPOSSales() },
                async { dataBaseRepository.clearPaymentTypes() },
                async { dataBaseRepository.clearPOSPendingSales() },
                async { dataBaseRepository.clearedPOSReceiptTemplate() },
                async { dataBaseRepository.clearPrinters() },
            )
            jobs.awaitAll()
        }
    }
}