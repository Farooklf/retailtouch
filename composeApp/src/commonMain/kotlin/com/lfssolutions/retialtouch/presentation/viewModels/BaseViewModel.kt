package com.lfssolutions.retialtouch.presentation.viewModels

import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.domain.model.login.AuthenticateDao
import com.lfssolutions.retialtouch.domain.ApiUtils.observeResponse
import com.lfssolutions.retialtouch.domain.ApiUtils.observeResponseNew
import com.lfssolutions.retialtouch.domain.PreferencesRepository
import com.lfssolutions.retialtouch.domain.RequestState
import com.lfssolutions.retialtouch.domain.model.AppState
import com.lfssolutions.retialtouch.domain.model.basic.BasicApiRequest
import com.lfssolutions.retialtouch.domain.model.employee.EmployeeDao
import com.lfssolutions.retialtouch.domain.model.employee.EmployeesResponse
import com.lfssolutions.retialtouch.domain.model.inventory.Stock
import com.lfssolutions.retialtouch.domain.model.location.LocationResponse
import com.lfssolutions.retialtouch.domain.model.login.LoginUiState
import com.lfssolutions.retialtouch.domain.model.memberGroup.MemberGroupResponse
import com.lfssolutions.retialtouch.domain.model.members.MemberDao
import com.lfssolutions.retialtouch.domain.model.members.MemberItem
import com.lfssolutions.retialtouch.domain.model.members.MemberResponse
import com.lfssolutions.retialtouch.domain.model.menu.CategoryDao
import com.lfssolutions.retialtouch.domain.model.menu.CategoryItem
import com.lfssolutions.retialtouch.domain.model.menu.CategoryResponse
import com.lfssolutions.retialtouch.domain.model.menu.MenuItem
import com.lfssolutions.retialtouch.domain.model.nextPOSSaleInvoiceNo.NextPOSSaleDao
import com.lfssolutions.retialtouch.domain.model.nextPOSSaleInvoiceNo.NextPOSSaleInvoiceNoResponse
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentTypeDao
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentTypeResponse
import com.lfssolutions.retialtouch.domain.model.posInvoice.POSInvoiceDao
import com.lfssolutions.retialtouch.domain.model.posInvoice.POSInvoiceResponse
import com.lfssolutions.retialtouch.domain.model.productBarCode.BarcodeDao
import com.lfssolutions.retialtouch.domain.model.productBarCode.ProductBarCodeResponse
import com.lfssolutions.retialtouch.domain.model.productLocations.ProductLocationDao
import com.lfssolutions.retialtouch.domain.model.productLocations.ProductLocationResponse
import com.lfssolutions.retialtouch.domain.model.productWithTax.ProductTaxDao
import com.lfssolutions.retialtouch.domain.model.productWithTax.ProductTaxItem
import com.lfssolutions.retialtouch.domain.model.productWithTax.ProductWithTaxByLocationResponse
import com.lfssolutions.retialtouch.domain.model.productWithTax.ScannedProductDao
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionResponse
import com.lfssolutions.retialtouch.domain.model.sync.SyncAllDao
import com.lfssolutions.retialtouch.domain.model.sync.SyncAllResponse
import com.lfssolutions.retialtouch.domain.model.sync.SyncItem
import com.lfssolutions.retialtouch.domain.model.terminal.TerminalResponse
import com.lfssolutions.retialtouch.domain.repositories.DataBaseRepository
import com.lfssolutions.retialtouch.domain.repositories.NetworkRepository
import com.lfssolutions.retialtouch.utils.AppConstants.CATEGORY
import com.lfssolutions.retialtouch.utils.AppConstants.EMPLOYEE_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.EMPLOYEE_ROLE_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.INVENTORY_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.LARGE_PHONE_MAX_WIDTH
import com.lfssolutions.retialtouch.utils.AppConstants.LOCATION_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.MEMBER_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.MENU_CATEGORY_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.MENU_PRODUCTS_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.PAYMENT_TYPE_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.PRODUCT
import com.lfssolutions.retialtouch.utils.AppConstants.PRODUCT_TAX_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.SMALL_PHONE_MAX_WIDTH
import com.lfssolutions.retialtouch.utils.AppConstants.SMALL_TABLET_MAX_WIDTH
import com.lfssolutions.retialtouch.utils.AppConstants.TERMINAL_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.DeviceType
import com.lfssolutions.retialtouch.utils.DoubleExtension.calculatePercentage
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
import kotlinx.coroutines.flow.map
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

    private val _composeAppState = MutableStateFlow(AppState())
    val composeAppState: StateFlow<AppState> = _composeAppState.asStateFlow()

    val _loginScreenState = MutableStateFlow(LoginUiState())
    val loginScreenState: StateFlow<LoginUiState> = _loginScreenState

    var count =0
    private val _authenticationDao = MutableStateFlow(AuthenticateDao())
    val authenticationDao: StateFlow<AuthenticateDao?> = _authenticationDao.asStateFlow()

    private val _lastSyncDateTime = MutableStateFlow<String?>(null)
    val lastSyncDateTime : StateFlow<String?>  get() = _lastSyncDateTime

    private val stockQtyMap = MutableStateFlow<Map<Int,Double?>>(emptyMap())

    private val _employeeDoa = MutableStateFlow<EmployeeDao?>(null)
    val employeeDoa = _employeeDoa.asStateFlow()
    
    private val _isMenuCategoryDbInserted = MutableStateFlow(false)
    val isMenuCategoryDbInserted: StateFlow<Boolean> get() = _isMenuCategoryDbInserted

    private val _categoryResponse = MutableStateFlow<List<CategoryItem>>(emptyList())
    val categoryResponse: StateFlow<List<CategoryItem?>> = _categoryResponse

    private val _isMenuProductDbInserted = MutableStateFlow(false)
    val isMenuProductDbInserted: StateFlow<Boolean> get() = _isMenuProductDbInserted

    private val _categoryItem = MutableStateFlow<CategoryItem?>(null)
    val categoryItem: StateFlow<CategoryItem?> = _categoryItem
    
    private val _isNEXTPOSSaleDbInserted = MutableStateFlow(false)
    val isNEXTPOSSaleDbInserted: StateFlow<Boolean> get() = _isNEXTPOSSaleDbInserted
    
    private val _isPOSInvoiceDbInserted = MutableStateFlow(false)
    val isPOSInvoiceDbInserted: StateFlow<Boolean> get() = _isPOSInvoiceDbInserted

    private val _isProductTaxDbInserted = MutableStateFlow(false)
    val isProductTaxDbInserted: StateFlow<Boolean> get() = _isProductTaxDbInserted
    
    private val _isProductLocationDbInserted = MutableStateFlow(false)
    val isProductLocationDbInserted: StateFlow<Boolean> get() = _isProductLocationDbInserted

    private val _isPaymentTypeDbInserted = MutableStateFlow(false)
    val isPaymentTypeDbInserted: StateFlow<Boolean> get() = _isPaymentTypeDbInserted

    private val _isMembersDbInserted = MutableStateFlow(false)
    val isMembersDbInserted: StateFlow<Boolean> get() = _isMembersDbInserted

    private val _isMemberGroupDbInserted = MutableStateFlow(false)
    val isMemberGroupDbInserted: StateFlow<Boolean> get() = _isMemberGroupDbInserted

    private val _isPromotionDbInserted = MutableStateFlow(false)
    val isPromotionDbInserted: StateFlow<Boolean> get() = _isPromotionDbInserted

    private val _isBarCodeDbInserted = MutableStateFlow(false)
    val isBarCodeDbInserted: StateFlow<Boolean> get() = _isBarCodeDbInserted

    private val _isSyncDbInserted = MutableStateFlow(false)
    val isSyncDbInserted: StateFlow<Boolean> get() = _isSyncDbInserted



    val _posInvoiceResponse = MutableStateFlow<POSInvoiceResponse?>(null)
    val _productTaxResponse = MutableStateFlow<ProductWithTaxByLocationResponse?>(null)
    val _productLocationResponse = MutableStateFlow<ProductLocationResponse?>(null)
    val _paymentTypeResponse = MutableStateFlow<PaymentTypeResponse?>(null)

    val _membersResponse = MutableStateFlow<MemberResponse?>(null)
    val membersResponse: StateFlow<MemberResponse?> get() = _membersResponse

    val _memberGroupResponse = MutableStateFlow<MemberGroupResponse?>(null)
    val memberGroupResponse: StateFlow<MemberGroupResponse?> get() = _memberGroupResponse

    val _promotionResponse = MutableStateFlow<PromotionResponse?>(null)
    val promotionResponse: StateFlow<PromotionResponse?> get() = _promotionResponse

    val _productBarCodeResponse = MutableStateFlow<ProductBarCodeResponse?>(null)
    val productBarCodeGroupResponse: StateFlow<ProductBarCodeResponse?> get() = _productBarCodeResponse

    val _syncResponse = MutableStateFlow<SyncAllResponse?>(null)
    val syncResponse: StateFlow<SyncAllResponse?> get() = _syncResponse




    // Expose the login state as a Flow<Boolean?>, with null indicating loading
    val isUserLoggedIn: StateFlow<Boolean?> = preferences.getUserLoggedIn()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = null
        )

    val authUser: StateFlow<AuthenticateDao?> = dataBaseRepository.getAuthUser()
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

    val employee: StateFlow<EmployeeDao?> = flow {
        // This flow emits the employee data asynchronously
        val employeeCode = preferences.getEmployeeCode().first()  // Suspends here
        emit(dataBaseRepository.getEmployee(employeeCode).firstOrNull())
    }.stateIn(
            scope = viewModelScope,  // Use viewModelScope
            started = SharingStarted.WhileSubscribed(5000),  // Keeps the flow alive for 5 seconds after subscription
            initialValue = null  // Initial state of the flow
        )


    val productsList: StateFlow<List<ProductTaxDao>> = dataBaseRepository.getProduct()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val memberList: StateFlow<List<MemberDao>> = dataBaseRepository.getMember()
       /* .map { daoList ->
            daoList.map { item ->
                mapMembersList(item)
            }
        }*/
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    private fun mapMembersList(memberDao: MemberDao): MemberItem {
        println("member List : ${memberDao.rowItem}")
        return memberDao.rowItem
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
            isPortrait = height > width
        )
        }
    }

    //Api Calls

    suspend fun getLocations(){
        try {
            //updateLoaderMsg("Fetching location data...")
            println("location calling api : ${count++}")
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

    suspend fun syncInventory(lastSyncTime:String?=null){
        try {
            updateLoaderMsg("Syncing Inventory")
            println("Syncing Inventory : ${count++}")
            networkRepository.getProductLocation(getBasicRequest()).collectLatest { stockAvailResponse->
                observeStock(stockAvailResponse)
            }
            val inventoryResponse=networkRepository.getProductsWithTax(getBasicRequest())
            val barcodesResponse=networkRepository.getProductBarCode(getBasicRequest())
            if(lastSyncTime!=null){
                networkRepository.getProductBarCode(getBarcodeRequest(true))
                dataBaseRepository.clearBarcode()
            }
            observeInventory(inventoryResponse,lastSyncTime)
            observeBarcode(barcodesResponse,lastSyncTime)

        }catch (e: Exception){
            val error="${e.message}"
            handleApiError(INVENTORY_ERROR_TITLE,error)
        }
    }

    suspend fun syncMembers(){
        try {
            updateLoaderMsg("Syncing Member")
            networkRepository.getMembers(getBasicTenantRequest()).collectLatest {apiResponse->
                observeMembers(apiResponse)
            }
        }catch (e: Exception){
            val error="${e.message}"
            handleApiError(MEMBER_ERROR_TITLE,error)
        }
    }

    suspend fun syncPaymentTypes(){
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

    suspend fun syncMemberGroup(){
        try {
            updateLoaderMsg("Syncing Member Group")
            networkRepository.getMemberGroup(getBasicTenantRequest()).collectLatest {apiResponse->
                observeMemberGroup(apiResponse)
            }
        }catch (e: Exception){
            val error="${e.message}"
            handleApiError(MEMBER_ERROR_TITLE,error)
        }
    }

    suspend fun syncCategories(){
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


    private fun observeCategory(apiResponse: RequestState<CategoryResponse>) {
        observeResponseNew(apiResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                if(apiData.success){
                    viewModelScope.launch {
                        _categoryResponse.update { apiData.result.items }
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

    suspend fun syncMenu(){
        val newStock : MutableList<MenuItem> = mutableListOf()
        _categoryResponse.value.forEach { cat->
            println("Menu products api : ${cat.id}")
            networkRepository.getMenuProducts(getBasicRequest(cat.id)).collectLatest {response->
                observeResponseNew(response,
                    onLoading = {  },
                    onSuccess = { menuData ->
                        if(menuData.success){
                            menuData.result.items.forEach { menu->
                                val updatedMenu=menu.copy(menuCategoryId=cat.id, id = if(menu.id==0) menu.productId else menu.id)
                                newStock.add(updatedMenu)
                            }
                            viewModelScope.launch {
                                dataBaseRepository.insertNewStock(newStock.map{mnu->
                                    Stock(
                                        id = mnu.id,
                                        name = mnu.name,
                                        icon = mnu.imagePath ?: "",
                                        categoryId = mnu.menuCategoryId,
                                        productId = mnu.productId,
                                        sortOrder = mnu.sortOrder,
                                        inventoryCode = mnu.inventoryCode,
                                        fgColor = mnu.foreColor ?: "",
                                        bgColor = mnu.backColor ?: "",
                                        barcode = mnu.barCode
                                    )})
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

    private suspend fun observeEmployees(apiResponse: Flow<RequestState<EmployeesResponse>>) {
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
                        println("member insertion : ${count++}")
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
            onLoading = {  },
            onSuccess = { apiData ->
                if(apiData.success){
                    viewModelScope.launch {
                        println("member group insertion : ${count++}")
                        dataBaseRepository.insertMemberGroup(apiData)
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
        lastSyncTime: String?
    ) {
        observeResponse(inventoryResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                viewModelScope.launch {
                    if(apiData.success){
                        dataBaseRepository.insertUpdateInventory(apiData,lastSyncTime,stockQtyMap.value)
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

    private fun observeStock(
        stockAvailResponse: RequestState<ProductLocationResponse>
    ) {
        println("menu category insertion : ${count++}")
        observeResponseNew(stockAvailResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                if(apiData.success){
                    stockQtyMap.update { oldMap ->
                        oldMap + apiData.result?.items?.associate { stock ->
                            stock.id to stock.qtyOnHand
                        }.orEmpty()
                    }
                }
            },
            onError = {
                    errorMsg ->
                handleApiError(MENU_CATEGORY_ERROR_TITLE,errorMsg)
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
                            _productBarCodeResponse.update { apiData }
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

    private fun observePaymentType(apiResponse: RequestState<PaymentTypeResponse>) {
        println("payment type insertion : ${count++}")

        observeResponseNew(apiResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                if(apiData.success){
                    viewModelScope.launch {
                        _paymentTypeResponse.update { apiData }
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

    private fun handleApiError(errorTitle:String, errorMsg: String) {
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



    private fun updateSyncDb(result:Boolean) {
        _isSyncDbInserted.update { result }
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

     private fun getBasicRequest(id:Int) = BasicApiRequest(
        id =id
    )

    private suspend fun getBasicTenantRequest() = BasicApiRequest(
        tenantId = preferences.getTenantId().first()
    )

    private suspend fun getBarcodeRequest(isDeleted:Boolean) = BasicApiRequest(
        tenantId = preferences.getTenantId().first(),
        locationId = preferences.getLocationId().first(),
        lastSyncDateTime = _lastSyncDateTime.value,
        isDeleted = isDeleted
    )

    suspend fun getLocationId() = preferences.getLocationId().first()
    suspend fun getTenantId() = preferences.getTenantId().first()

   suspend fun getUserId() :Long{
        return preferences.getUserId().first()
    }

    fun resetStates() {
        viewModelScope.launch {
            val jobs = listOf(
                async { _authenticationDao.update { AuthenticateDao() } },
                async { _employeeDoa.update { EmployeeDao() } },
                async { _categoryResponse.update { emptyList()} },
            )
            jobs.awaitAll()
        }
    }

    fun emptyLocalPref(){
        viewModelScope.launch {
            val jobs = listOf(
                async { preferences.setBaseURL("") },
                async { preferences.setToken("") },
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