package com.lfssolutions.retialtouch.presentation.viewModels

import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.domain.model.login.AuthenticateDao
import com.lfssolutions.retialtouch.domain.ApiUtils.observeResponse
import com.lfssolutions.retialtouch.domain.ApiUtils.observeResponseNew
import com.lfssolutions.retialtouch.domain.PreferencesRepository
import com.lfssolutions.retialtouch.domain.RequestState
import com.lfssolutions.retialtouch.domain.model.ApiLoaderStateResponse
import com.lfssolutions.retialtouch.domain.model.AppState
import com.lfssolutions.retialtouch.domain.model.basic.BasicApiRequest
import com.lfssolutions.retialtouch.domain.model.employee.EmployeeDao
import com.lfssolutions.retialtouch.domain.model.employee.EmployeesResponse
import com.lfssolutions.retialtouch.domain.model.products.Stock
import com.lfssolutions.retialtouch.domain.model.location.LocationResponse
import com.lfssolutions.retialtouch.domain.model.login.LoginRequest
import com.lfssolutions.retialtouch.domain.model.login.LoginUiState
import com.lfssolutions.retialtouch.domain.model.memberGroup.MemberGroupResponse
import com.lfssolutions.retialtouch.domain.model.members.MemberResponse
import com.lfssolutions.retialtouch.domain.model.menu.CategoryItem
import com.lfssolutions.retialtouch.domain.model.menu.CategoryResponse
import com.lfssolutions.retialtouch.domain.model.menu.MenuItem
import com.lfssolutions.retialtouch.domain.model.nextPOSSaleInvoiceNo.NextPOSSaleInvoiceNoResponse
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentTypeResponse
import com.lfssolutions.retialtouch.domain.model.printer.GetPrintTemplateRequest
import com.lfssolutions.retialtouch.domain.model.printer.GetPrintTemplateResult
import com.lfssolutions.retialtouch.domain.model.printer.PrinterTemplates
import com.lfssolutions.retialtouch.domain.model.invoiceSaleTransactions.POSInvoiceRequest
import com.lfssolutions.retialtouch.domain.model.invoiceSaleTransactions.GetPosInvoiceResult
import com.lfssolutions.retialtouch.domain.model.location.Location
import com.lfssolutions.retialtouch.domain.model.posInvoices.PendingSale
import com.lfssolutions.retialtouch.domain.model.posInvoices.PendingSaleDao
import com.lfssolutions.retialtouch.domain.model.productBarCode.ProductBarCodeResponse
import com.lfssolutions.retialtouch.domain.model.productLocations.ProductLocationResponse
import com.lfssolutions.retialtouch.domain.model.products.CreatePOSInvoiceRequest
import com.lfssolutions.retialtouch.domain.model.products.PosInvoice
import com.lfssolutions.retialtouch.domain.model.products.ProductWithTaxByLocationResponse
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionRequest
import com.lfssolutions.retialtouch.domain.model.promotions.GetPromotionResult
import com.lfssolutions.retialtouch.domain.model.promotions.GetPromotionsByPriceResult
import com.lfssolutions.retialtouch.domain.model.promotions.GetPromotionsByQtyResult
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionDetails
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionItem
import com.lfssolutions.retialtouch.domain.model.sync.SyncItem
import com.lfssolutions.retialtouch.domain.model.terminal.TerminalResponse
import com.lfssolutions.retialtouch.domain.repositories.DataBaseRepository
import com.lfssolutions.retialtouch.domain.repositories.NetworkRepository
import com.lfssolutions.retialtouch.utils.AppConstants.CATEGORY
import com.lfssolutions.retialtouch.utils.AppConstants.EMPLOYEE_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.EMPLOYEE_ROLE_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.INVENTORY_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.INVOICE
import com.lfssolutions.retialtouch.utils.AppConstants.LARGE_PHONE_MAX_WIDTH
import com.lfssolutions.retialtouch.utils.AppConstants.LOCATION_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.MEMBER_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.MENU_CATEGORY_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.MENU_PRODUCTS_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.NEXT_SALE_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.PAYMENT_TYPE_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.PRODUCT
import com.lfssolutions.retialtouch.utils.AppConstants.PROMOTION
import com.lfssolutions.retialtouch.utils.AppConstants.PROMOTIONS_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.SMALL_PHONE_MAX_WIDTH
import com.lfssolutions.retialtouch.utils.AppConstants.SMALL_TABLET_MAX_WIDTH
import com.lfssolutions.retialtouch.utils.AppConstants.SYNC_SALES_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.SYNC_TEMPLATE_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.TERMINAL_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.DateTimeUtils.getCurrentDateAndTimeInEpochMilliSeconds
import com.lfssolutions.retialtouch.utils.DateTimeUtils.getHoursDifferenceFromEpochMillSeconds
import com.lfssolutions.retialtouch.utils.DeviceType
import com.lfssolutions.retialtouch.utils.PrefKeys.TOKEN_EXPIRY_THRESHOLD
import com.lfssolutions.retialtouch.utils.TemplateType
import com.lfssolutions.retialtouch.utils.serializers.db.parsePriceBreakPromotionAttributes
import com.lfssolutions.retialtouch.utils.serializers.db.toDefaultLocation
import com.lfssolutions.retialtouch.utils.serializers.db.toJson
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
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class BaseViewModel: ViewModel(), KoinComponent {

    val networkRepository: NetworkRepository by inject()
    val preferences: PreferencesRepository by inject()
    val dataBaseRepository: DataBaseRepository by inject()

    private val _composeAppState = MutableStateFlow(AppState())
    val composeAppState: StateFlow<AppState> = _composeAppState.asStateFlow()

    private val _posSaleApiState: MutableStateFlow<ApiLoaderStateResponse> = MutableStateFlow(ApiLoaderStateResponse.Loader)
    val posSaleApiState: StateFlow<ApiLoaderStateResponse> = _posSaleApiState.asStateFlow()

    val _loginScreenState = MutableStateFlow(LoginUiState())
    val loginScreenState: StateFlow<LoginUiState> = _loginScreenState

    var count =0

    private val _isPrinterEnabled = MutableStateFlow(false)
    val isPrinterEnabled : StateFlow<Boolean>  get() = _isPrinterEnabled


    private var refreshingToken : StateFlow<Boolean> = MutableStateFlow(false)

    private val _lastSyncDateTime = MutableStateFlow<String?>(null)
    val lastSyncDateTime : StateFlow<String?>  get() = _lastSyncDateTime

    private val stockQtyMap = MutableStateFlow<Map<Int,Double?>>(emptyMap())

    val employeeDoa = MutableStateFlow<EmployeeDao?>(null)

    private val categoryResponse = MutableStateFlow<List<CategoryItem>>(emptyList())

    private val promotionResult = MutableStateFlow<GetPromotionResult?>(null)
    private val paymentTypeResponse = MutableStateFlow<PaymentTypeResponse?>(null)
    private val productBarCodeResponse = MutableStateFlow<ProductBarCodeResponse?>(null)

    private val _printerTemplates = MutableStateFlow<List<PrinterTemplates>?>(emptyList())
    val printerTemplates : StateFlow<List<PrinterTemplates>?> = _printerTemplates.asStateFlow()

    private val _syncInProgress = MutableStateFlow(false)
    val syncInProgress: StateFlow<Boolean> = _syncInProgress.asStateFlow()

    private val _syncProgressStatus = MutableStateFlow("")
    val syncProgressStatus: StateFlow<String> = _syncProgressStatus.asStateFlow()

    private val _syncError = MutableStateFlow(false)
    val syncError: StateFlow<Boolean> = _syncError.asStateFlow()

    private val _syncErrorInfo = MutableStateFlow("")
    val syncErrorInfo: StateFlow<String> = _syncErrorInfo.asStateFlow()

    private val _lastSyncTs = MutableStateFlow(0L)
    val lastSyncTs: StateFlow<Long> = _lastSyncTs.asStateFlow()

    private val _resyncTimer = MutableStateFlow(0L)
    val resyncTimer: StateFlow<Long> = _resyncTimer.asStateFlow()


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

    val isPrinterEnable: StateFlow<Boolean?> = preferences.getIsPrinterEnabled()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = null
        )

    val employee: StateFlow<EmployeeDao?> = flow {
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

     fun updatePrinterValue(isPrinter: Boolean) {
        viewModelScope.launch {
            _isPrinterEnabled.update { isPrinter }
            preferences.setIsPrinterEnabled(isPrinter)
        }
    }

    private fun updateSyncProgress(syncStatus: Boolean) {
        _syncInProgress.update { syncStatus }
    }

    private fun updateSyncStatus(syncStatus: String) {
        _syncProgressStatus.update { syncStatus }
    }

     fun updateLastSyncTs(syncStatus: Long) {
        _lastSyncTs.update { syncStatus }
    }
    // Handle any sync errors
    private fun handleError(error:Boolean,errorTitle: String, errorMsg: String) {
        _syncError.update { error }
        _syncErrorInfo.update {  "$errorTitle \n $errorMsg" }
    }

    private fun updateUIStatus(syncStatus: Boolean) {
        _uiUpdateStatus.update { syncStatus }
    }
    //check if user logged in or not
    suspend fun isLoggedIn() : Boolean {
        val tokenTime : Long = getLastTokenTime()
        val currentTime = getCurrentDateAndTimeInEpochMilliSeconds()
        val hoursPassed = getHoursDifferenceFromEpochMillSeconds(tokenTime, currentTime)
        if(hoursPassed > TOKEN_EXPIRY_THRESHOLD){
            refreshToken()
        }
        return true
    }


    private fun refreshToken(){
        viewModelScope.launch {
            try {
                val response = async {
                    networkRepository.hitLoginAPI(getLoginDetails())
                }.await()
                observeResponse(response,
                    onLoading = {

                    },
                    onSuccess = {loginRes->
                       viewModelScope.launch {
                           if(loginRes.success==true){
                               preferences.setToken(loginRes.result?:"")
                           }
                       }
                    },
                    onError = {

                    }
                )

            }catch (ex:Exception){
              //Go to Login Page

            }
        }
    }

    private suspend fun getLoginDetails(): LoginRequest {
        val loginRequest = LoginRequest(
            usernameOrEmailAddress = preferences.getUserName().first(),
            tenancyName = preferences.getTenancyName().first(),
            password =   preferences.getUserPass().first(),
        )
        return loginRequest

    }

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
            posInvoiceDetails = data.posInvoiceDetailRecord,
            posPayments = data.posPaymentConfigRecord,
            qty = data.qty,
            customerName = data.memberName,
            address1 = data.address1 ,
            address2 = data.address2 ,
        )

        return posInvoice
    }


    //Api Calls

    suspend fun getLocations(location: String) {
        try {
            //updateLoaderMsg("Fetching location data...")
            println("location Calling api : ${count++}")
            val loginApiResponse=networkRepository.getLocationForUser(getBasicRequest())
            observeLocation(loginApiResponse)
        }catch (e:Exception){
            handleApiError(LOCATION_ERROR_TITLE,e.message.toString())
        }
    }

    suspend fun getEmployees(){
        try {
            updateLoaderMsg("Fetching employees data...")
            println("employees calling api : ${count++}")
            val employeesResponse=networkRepository.getEmployees(getBasicRequest())
            observeEmployees(employeesResponse)
        }catch (e: Exception){
            val error=e.message.toString()
            handleApiError(EMPLOYEE_ERROR_TITLE,error)
        }
    }

    suspend fun getEmployeeRole(){
        try {
            updateLoaderMsg("Fetching employees role data...")
            println("employees role calling api : ${count++}")
            val empRoleResponse=networkRepository.getEmployeeRole(getBasicRequest())
            observeEmpRole(empRoleResponse)
        }catch (e: Exception){
            val error="${e.message}"
            handleApiError(EMPLOYEE_ROLE_ERROR_TITLE,error)
        }
    }

    fun syncEmployeeRights(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateLoaderMsg("Syncing Employee Rights")
                networkRepository.getEmployeeRights(BasicApiRequest(
                    tenantId = getTenantId(),
                    name = employeeDoa.value?.employeeRoleName
                )).collectLatest {apiResponse->
                    observeResponseNew(apiResponse,
                        onLoading = {  },
                        onSuccess = { apiData ->
                            if(apiData.success){
                                viewModelScope.launch {
                                    dataBaseRepository.insertEmpRights(apiData)
                                    println("employee rights insertion : ${count++}")
                                }
                            }
                        },
                        onError = {
                                errorMsg ->
                            handleApiError(EMPLOYEE_ERROR_TITLE,errorMsg)
                        }
                    )
                }
            }catch (e: Exception){
                val error="${e.message}"
                handleApiError(EMPLOYEE_ERROR_TITLE,error)
            }
        }
    }

    fun syncEveryThing2(){
       try {
         viewModelScope.launch(Dispatchers.IO) {
             updateSyncProgress(true)

             val pendingCount = dataBaseRepository.getAllPendingSaleRecordsCount().first()
             if (pendingCount <= 0) {
                 loadNextSalesInvoiceNumber2()
             } else {
                 updateSyncStatus("Invoice Number Error', 'Sync Pending Invoice First ")
             }

             syncMembers()
             syncMemberGroup()
             syncSales()
             syncStockQuantity()
             syncInventory()
             syncCategories()
             syncPromotion()
             syncPaymentTypes()
             println("All Sync Operations Completed Successfully")
             updateSyncStatus("All Sync Operations have been Completed Successfully")
             updateSyncProgress(false)
             preferences.setLastSyncTs(getCurrentDateAndTimeInEpochMilliSeconds())
         }
       }catch (e: Exception){
           val error = "failed during sync: ${e.message}"
           println("SYNC_ERROR $error")
           handleError(true,"SYNC_ERROR", error)
           updateSyncProgress(false)
       }
    }

    suspend fun loadNextSalesInvoiceNumber2(){
        try {
            println("API CALL : ${count++}")
            updateSyncStatus("loading sale invoice count")
            networkRepository.getNextPOSSaleInvoice(getBasicRequest()).collectLatest {apiResponse->
                observeNextSaleInvoices(apiResponse)
            }
        }catch (e: Exception){
            val error="${e.message}"
            handleError(true,NEXT_SALE_ERROR_TITLE,error)
        }
    }

     private suspend fun syncStockQuantity(lastSyncTime:String?=null){
        try {
            println("API CALL : ${count++}")
            updateLoaderMsg("Syncing Inventory Count")
            networkRepository.getProductLocation(getBasicRequest()).collectLatest {stockAvailResponse->
                observeStock(stockAvailResponse,lastSyncTime)
                //println("QtyMap: $stockQtyMap")
            }
        }catch (e: Exception){
            val error="${e.message}"
            handleApiError(INVENTORY_ERROR_TITLE,error)
        }
    }

     suspend fun syncInventory(lastSyncTime:String?=null){
        try {
            updateLoaderMsg("Syncing Inventory")
            println("Inventory API CALL : ${count++}")
            val inventoryResponse=networkRepository.getProductsWithTax(getBasicRequest())
            val barcodesResponse=networkRepository.getProductBarCode(getBasicRequest())
            if(lastSyncTime!=null){
                networkRepository.getProductBarCode(getBarcodeRequest(true))
                dataBaseRepository.clearBarcode()
            }
            observeInventory(inventoryResponse,stockQtyMap.value,lastSyncTime)
            observeBarcode(barcodesResponse,lastSyncTime)

        }catch (e: Exception){
            val error="${e.message}"
            handleApiError(INVENTORY_ERROR_TITLE,error)
        }
    }

    private suspend fun syncMembers(){
        try {
            println("Members API CALL : ${count++}")
            updateLoaderMsg("Syncing Member")
            networkRepository.getMembers(getBasicTenantRequest()).collectLatest {apiResponse->
                observeMembers(apiResponse)
            }
        }catch (e: Exception){
            val error="${e.message}"
            handleApiError(MEMBER_ERROR_TITLE,error)
        }
    }

    private suspend fun syncPaymentTypes(){
        try {
            updateLoaderMsg("Syncing Payment Type")
            networkRepository.getPaymentTypes(getBasicTenantRequest()).collectLatest {apiResponse->
                observePaymentType(apiResponse)
            }
        }catch (e: Exception){
            val error="${e.message}"
            handleApiError(PAYMENT_TYPE_ERROR_TITLE,error)
        }
    }

    private suspend fun syncMemberGroup(){
        try {
            updateLoaderMsg("Syncing Member Group")
            println("MemberGroup API CALL : ${count++}")
            networkRepository.getMemberGroup(getBasicTenantRequest()).collectLatest {apiResponse->
                observeMemberGroup(apiResponse)
            }
        }catch (e: Exception){
            val error="${e.message}"
            handleApiError(MEMBER_ERROR_TITLE,error)
        }
    }

    private suspend fun syncSales(){
        try {
            updateLoaderMsg("Syncing Sales History")
            println("API CALL : ${count++}")
            getLatestSale(skipCount = 0, maxResultCount = 1).collect{sale->
                when(sale){
                    is RequestState.Error -> {
                        handleApiError(SYNC_SALES_ERROR_TITLE,sale.message)
                    }
                    is RequestState.Idle -> {

                    }
                    is RequestState.Loading -> {

                    }
                    is RequestState.Success -> {
                        val totalCount = sale.data.result?.totalCount
                        if(totalCount!=null && totalCount>=1000){
                            getLatestSale(skipCount = totalCount-600, maxResultCount = 1000).collect { response->
                                SaveSyncSales(response)
                            }
                        }else{
                            SaveSyncSales(sale)
                        }
                    }
                }
            }
        }catch (e: Exception){
            val error="${e.message}"
            handleApiError(SYNC_SALES_ERROR_TITLE,error)
        }
    }

    private fun SaveSyncSales(apiResponse: RequestState<GetPosInvoiceResult>) {
        observeResponseNew(apiResponse,
            onLoading = {
                updateSyncProgress(true)
            },
            onSuccess = { apiData ->
                if(apiData.success){
                    viewModelScope.launch {
                        dataBaseRepository.insertNewLatestSales(apiData)
                        updateSyncGrid(INVOICE)
                        //set
                        updateLastSyncTs(getCurrentDateAndTimeInEpochMilliSeconds())
                    }
                }
            },
            onError = {
                    errorMsg ->
                handleError(true,SYNC_SALES_ERROR_TITLE,errorMsg)
            }
        )

    }

    private suspend fun syncCategories(){
        try {
            updateLoaderMsg("Syncing Categories")
            networkRepository.getMenuCategories(getBasicRequest()).collectLatest {apiResponse->
                observeCategory(apiResponse)
            }
        }catch (e: Exception){
            val error="${e.message}"
            handleApiError(MENU_CATEGORY_ERROR_TITLE,error)
        }
    }

    suspend fun syncMenu(){
        val newStock : MutableList<MenuItem> = mutableListOf()
        categoryResponse.value.forEach { cat->
            println("Menu products api : ${cat.id}")
            networkRepository.getMenuProducts(getBasicRequest(cat.id?:0)).collectLatest {response->
                observeResponseNew(response,
                    onLoading = {  },
                    onSuccess = { menuData ->
                        if(menuData.success){
                            menuData.result.items.forEach { menu->
                                val updatedMenu=menu.copy(menuCategoryId=cat.id?:0, id = if(menu.id==0L) menu.productId else menu.id)
                                newStock.add(updatedMenu)
                            }
                            viewModelScope.launch {
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
                            }
                        }
                    },
                    onError = {
                            errorMsg ->
                        handleApiError(MENU_PRODUCTS_ERROR_TITLE,errorMsg)
                    }
                )
            }
        }
    }

    private suspend fun syncPromotion(){
        try {
            updateLoaderMsg("Syncing Promotions")
            networkRepository.getPromotions(PromotionRequest(tenantId = getTenantId())).collectLatest { apiResponse->
                observePromotions(apiResponse)
            }
        }catch (e: Exception){
            val error="${e.message}"
            handleApiError(PROMOTIONS_ERROR_TITLE,error)
        }
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
           val error="${e.message}"
           handleError(true,SYNC_SALES_ERROR_TITLE,error)
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

    suspend fun syncPrintTemplate(templateType: TemplateType){
        try {
            if(_syncInProgress.value)
                return

            println("Syncing Print Template: ${templateType.toInt()}")
            updateSyncStatus("Syncing Print Template")
            networkRepository.getPrintTemplate(GetPrintTemplateRequest(locationId = getLocationId()?:0, type = templateType.toInt())).collect { apiResponse->
                observePrintTemplate(apiResponse)
            }
        }catch (e: Exception){
            val error="${e.message}"
            handleError(true,SYNC_TEMPLATE_ERROR_TITLE,error)
            updateSyncProgress(false)
            println("Syncing Print Template: $error")
        }
    }


    /*------Api Call End--------*/

    private fun observePrintTemplate(apiResponse: RequestState<GetPrintTemplateResult>) {
        observeResponseNew(apiResponse,
            onLoading = {
                updateSyncProgress(true)
                        },
            onSuccess = { apiData ->
                println("print template insertion : ${count++}")
                if(apiData.success){
                    _printerTemplates.update { apiData.result }
                }else{
                    handleError(true,SYNC_TEMPLATE_ERROR_TITLE,apiData.error?.message?:"template not found")
                }
                updateSyncProgress(false)
            },
            onError = { errorMsg ->
                handleError(true,SYNC_TEMPLATE_ERROR_TITLE,errorMsg)
                updateSyncProgress(false)
            }
        )
    }

    private fun observeNextSaleInvoices(apiResponse: RequestState<NextPOSSaleInvoiceNoResponse>) {
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
            onError = {
                    errorMsg ->
                handleError(true,NEXT_SALE_ERROR_TITLE,errorMsg)
            }
        )

    }

    private fun observeLatestSales(apiResponse: RequestState<GetPosInvoiceResult>) {
        observeResponseNew(apiResponse,
            onLoading = {
                updateSyncProgress(true)
            },
            onSuccess = { apiData ->
                if(apiData.success){
                    viewModelScope.launch {
                        dataBaseRepository.insertNewLatestSales(apiData)
                        updateSyncGrid(INVOICE)
                        //set
                        updateLastSyncTs(getCurrentDateAndTimeInEpochMilliSeconds())
                        updateSyncProgress(false)
                        updateSyncStatus("")
                        handleError(false,"","")
                        updateUIStatus(true)
                    }
                }
            },
            onError = {
                    errorMsg ->
                handleError(true,SYNC_SALES_ERROR_TITLE,errorMsg)
                updateSyncProgress(false)
                updateUIStatus(true)
            }
        )

    }

    private fun observeCategory(apiResponse: RequestState<CategoryResponse>) {
        observeResponseNew(apiResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                if(apiData.success){
                    viewModelScope.launch {
                        categoryResponse.update { apiData.result.items }
                        dataBaseRepository.insertCategories(apiData)
                        updateSyncGrid(CATEGORY)
                        //set
                        syncMenu()
                    }
                }
            },
            onError = {
                    errorMsg ->
                handleApiError(MENU_CATEGORY_ERROR_TITLE,errorMsg)
            }
        )
    }

    private suspend fun observeLocation(apiResponse: Flow<RequestState<LocationResponse>>) {
        println("location insertion : ${count++}")
        observeResponse(apiResponse,
            onLoading = { updateLoaderMsg("Syncing Location")},
            onSuccess = { apiData ->
                viewModelScope.launch {
                    dataBaseRepository.insertLocation(apiData)
                    //
                }
            },
            onError = {
                    errorMsg ->
                handleApiError(LOCATION_ERROR_TITLE,errorMsg)
            }
        )
    }

    suspend fun observeEmployees(apiResponse: Flow<RequestState<EmployeesResponse>>) {
        println("employees insertion : ${count++}")
        observeResponse(apiResponse,
            onLoading = {  updateLoaderMsg("Syncing Employees")},
            onSuccess = { apiData ->
                viewModelScope.launch {
                    dataBaseRepository.insertEmployees(apiData)
                } },
            onError = {errorMsg->
                handleApiError(EMPLOYEE_ERROR_TITLE,errorMsg)
            }
        )
    }

    private suspend fun observeEmpRole(apiResponse: Flow<RequestState<EmployeesResponse>>) {
        println("employees role insertion : ${count++}")
        observeResponse(apiResponse,
            onLoading = {  updateLoaderMsg("Syncing Employees Role")},
            onSuccess = { apiData ->
                viewModelScope.launch {
                    dataBaseRepository.insertEmpRole(apiData)
                } },
            onError = {errorMsg->
                handleApiError(EMPLOYEE_ROLE_ERROR_TITLE,errorMsg)
            }
        )
    }

    private fun observeMembers(apiResponse: RequestState<MemberResponse>) {
        observeResponseNew(apiResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                if(apiData.success){
                    viewModelScope.launch {
                        dataBaseRepository.insertMembers(apiData)
                        println("API CALL : ${count++}")
                    }
                }
            },
            onError = {
                    errorMsg ->
                handleApiError(MEMBER_ERROR_TITLE,errorMsg)
            }
        )
    }

    private fun observeMemberGroup(apiResponse: RequestState<MemberGroupResponse>) {
        observeResponseNew(apiResponse,
            onLoading = {

            },
            onSuccess = { apiData ->
                if(apiData.success){
                    viewModelScope.launch {
                        dataBaseRepository.insertMemberGroup(apiData)
                        println("MemberGroup Insertion : ${count++}")
                    }
                }
            },
            onError = {
                    errorMsg ->
                handleApiError(MEMBER_ERROR_TITLE,errorMsg)
            }
        )
    }

    private suspend  fun observeInventory(
        inventoryResponse: Flow<RequestState<ProductWithTaxByLocationResponse>>,
        stockQtyMap: Map<Int, Double?>,
        lastSyncTime: String?
    ) {
        observeResponse(inventoryResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                viewModelScope.launch {
                    if(apiData.success){
                        dataBaseRepository.insertUpdateInventory(apiData,lastSyncTime, stockQtyMap)
                        updateSyncGrid(PRODUCT)
                    }
                }
            },
            onError = {
                    errorMsg ->
                handleApiError(INVENTORY_ERROR_TITLE,errorMsg)
            }
        )
    }

    private suspend fun observeStock(
        stockAvailResponse: RequestState<ProductLocationResponse>,
        lastSyncTime: String?
    )  {
        val updatedStockQtyMap: MutableMap<Int, Double?> = mutableMapOf()

        observeResponseNew(stockAvailResponse,
            onLoading = { },
            onSuccess = { apiData ->
                if (apiData.success) {
                    viewModelScope.launch {
                        dataBaseRepository.insertUpdateProductQuantity(apiData,lastSyncTime)
                        println("Product Quantity insertion: ${count++}")
                    }
                    apiData.result?.items?.forEach { stock ->
                        updatedStockQtyMap[stock.productId]=stock.qtyOnHand
                    }
                    stockQtyMap.update { oldMap -> oldMap + updatedStockQtyMap }
                }
            },
            onError = { errorMsg ->
                handleApiError(MENU_CATEGORY_ERROR_TITLE, errorMsg)

            }
        )
    }


    private  fun observeBarcode(
        barcodesResponse: Flow<RequestState<ProductBarCodeResponse>>,
        lastSyncTime: String?
    ) {
        viewModelScope.launch {
            observeResponse(barcodesResponse,
                onLoading = {  },
                onSuccess = { apiData ->
                    viewModelScope.launch {
                        if(apiData.success){
                            productBarCodeResponse.update { apiData }
                            dataBaseRepository.insertUpdateBarcode(apiResponse = apiData,lastSyncDateTime=lastSyncTime)
                        }
                    }
                },
                onError = {
                        errorMsg ->
                    handleApiError(INVENTORY_ERROR_TITLE,errorMsg)
                }
            )
        }
    }

    private fun observePromotions(apiResponse: RequestState<GetPromotionResult>) {
        observeResponseNew(apiResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                if(apiData.success){
                    viewModelScope.launch {
                        promotionResult.update { apiData}
                        dataBaseRepository.insertPromotions(apiData)
                        //get
                        mapPromotions()
                        updateSyncGrid(PROMOTION)
                        //set syncToken

                    }
                }
            },
            onError = {
                    errorMsg ->
                handleApiError(PROMOTIONS_ERROR_TITLE,errorMsg)
            }
        )
    }

    private suspend fun mapPromotions(){
        promotionResult.collectLatest {
            it?.result?.forEach { item->
            if(item!=null){
                when(item.promotionTypeName){
                    "PromotionByQty"->{
                        networkRepository.getPromotionsByQty(PromotionRequest(id = item.id.toInt())).collectLatest { promotionsData->
                           observePromotionsQty(promotionsData,item)
                        }
                    }

                    "PromotionByPrice" ->{
                        networkRepository.getPromotionsByPrice(PromotionRequest(id = item.id.toInt())).collectLatest { promotionsData->
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
        }
    }

     private fun observePromotionsQty(
        apiResponse: RequestState<GetPromotionsByQtyResult>,
        promotion: PromotionItem
    ){
        observeResponseNew(apiResponse,
            onLoading = { updateLoaderMsg("Syncing Promotion QTY....")},
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
                handleApiError(PROMOTIONS_ERROR_TITLE,errorMsg)
            }
        )
    }

     private fun observePromotionPrice(
        apiResponse: RequestState<GetPromotionsByPriceResult>,
        promotion: PromotionItem
    ){
        observeResponseNew(apiResponse,
            onLoading = { updateLoaderMsg("Syncing Promotion By Price....")},
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
                handleApiError(PROMOTIONS_ERROR_TITLE,errorMsg)
            }
        )
    }


     private fun observePromotionByPriceBreak(
        apiResponse: RequestState<GetPromotionsByQtyResult>,
        promotion: PromotionItem
    ){
        observeResponseNew(apiResponse,
            onLoading = { updateLoaderMsg("Syncing Promotion By Price Break....")},
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
                handleApiError(PROMOTIONS_ERROR_TITLE,errorMsg)
            }
        )
    }

     private fun observeDefaultPromotions(
        apiResponse: RequestState<GetPromotionsByQtyResult>,
        promotion: PromotionItem
    ){
        observeResponseNew(apiResponse,
            onLoading = { updateLoaderMsg("Syncing Promotion Default....")},
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
                handleApiError(PROMOTIONS_ERROR_TITLE,errorMsg)
            }
        )
    }

    private fun observePaymentType(apiResponse: RequestState<PaymentTypeResponse>) {
        println("payment type insertion : ${count++}")

        observeResponseNew(apiResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                if(apiData.success){
                    viewModelScope.launch {
                        paymentTypeResponse.update { apiData }
                        dataBaseRepository.insertPaymentType(apiData)
                    }
                }
            },
            onError = {
                    errorMsg ->
                handleApiError(MEMBER_ERROR_TITLE,errorMsg)
            }
        )
    }

    suspend fun getTerminal(){
        try {
            updateLoaderMsg("Fetching terminal data...")
            println("terminal calling api : ${count++}")
            networkRepository.getTerminal(getBasicRequest()).collectLatest{terminalResponse->
                observeTerminal(terminalResponse)
            }
        }catch (e: Exception){
            val error="${e.message}"
            handleApiError(TERMINAL_ERROR_TITLE,error)
        }
    }

    private fun observeTerminal(apiResponse: RequestState<TerminalResponse>) {
        println("terminal insertion : ${count++}")
        observeResponseNew(apiResponse,
            onLoading = { updateLoaderMsg("Fetching Terminal....")},
            onSuccess = { apiData -> /*inser(locationData)*/ },
            onError = { errorMsg ->
                handleApiError(TERMINAL_ERROR_TITLE,errorMsg)
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

    fun updateLoaderMsg(loadingMsg:String) {
        viewModelScope.launch {
            _loginScreenState.value = _loginScreenState.value.copy(
                loadingMessage = loadingMsg
            )
        }
    }

     fun handleApiError(errorTitle:String, errorMsg: String) {
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

    suspend fun getCurrencySymbol() : String{
        return preferences.getCurrencySymbol().first()
    }


    private fun updateSyncGrid(name: String): SyncItem {
        /*with(_syncDataState.value){
            return syncerGuid.items.firstOrNull{ it.name.uppercase() == name }
                ?: SyncItem(name = name, syncerGuid = "", id = 0)
        }*/
        return SyncItem()
    }


    suspend fun setEmployeeCode(code:String){
        preferences.setEmployeeCode(code)
    }

    fun setUserLoggedIn(result:Boolean){
        viewModelScope.launch {
            preferences.setUserLoggedIn(result)
        }
    }

    suspend fun getBasicRequest() = BasicApiRequest(
        tenantId = preferences.getTenantId().first(),
        locationId = preferences.getLocationId().first(),
        lastSyncDateTime = _lastSyncDateTime.value
        )

    fun getBasicRequest(id:Int) = BasicApiRequest(
        id =id
    )

    private suspend fun getBasicTenantRequest() = BasicApiRequest(
        tenantId = preferences.getTenantId().first()
    )

    suspend fun getBarcodeRequest(isDeleted:Boolean) = BasicApiRequest(
        tenantId = preferences.getTenantId().first(),
        locationId = preferences.getLocationId().first(),
        lastSyncDateTime = _lastSyncDateTime.value,
        isDeleted = isDeleted
    )

    suspend fun getLocationId() = dataBaseRepository.getSelectedLocation().first()?.locationId?.toInt()
    suspend fun getTenantId() = preferences.getTenantId().first()
    suspend fun getLocation() = dataBaseRepository.getSelectedLocation().first()

    suspend fun getUserId() :Long{
        return preferences.getUserId().first()
    }

    suspend fun getLastTokenTime() : Long{
        return preferences.getTokenTime().first()
    }

    suspend fun getEmpCode() :String{
        return preferences.getEmployeeCode().first()
    }

    suspend fun setDefaultLocation(location: Location){
        preferences.setLocation(location.toJson())
    }

    suspend fun getDefaultLocation():Location{
        return preferences.getLocation().first().toDefaultLocation()
    }

    suspend fun getLocationName():String{
        return preferences.getLocation().first().toDefaultLocation().name
    }


    fun resetStates() {
        viewModelScope.launch {
            val jobs = listOf(
                async { employeeDoa.update { EmployeeDao() } },
                async { categoryResponse.update { emptyList()} },
            )
            jobs.awaitAll()
        }
    }

    fun emptyLocalPref(){
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
                async { preferences.setUserLoggedIn(false) },
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
                async { dataBaseRepository.clearMember() },
                async { dataBaseRepository.clearMemberGroup() },
                async { dataBaseRepository.clearInventory() },
                async { dataBaseRepository.clearBarcode() },
                async { dataBaseRepository.clearCategory() },
                async { dataBaseRepository.clearStocks() },
                async { dataBaseRepository.clearScannedProduct() }
            )
            jobs.awaitAll()
        }
    }
}