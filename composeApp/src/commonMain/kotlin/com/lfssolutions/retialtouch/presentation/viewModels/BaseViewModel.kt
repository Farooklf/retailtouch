package com.lfssolutions.retialtouch.presentation.viewModels

import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.domain.model.login.AuthenticateDao
import com.lfssolutions.retialtouch.domain.model.login.LoginResponse
import com.lfssolutions.retialtouch.dataBase.DatabaseRepository
import com.lfssolutions.retialtouch.domain.ApiUtils.observeResponse
import com.lfssolutions.retialtouch.domain.ApiUtils.observeResponseNew
import com.lfssolutions.retialtouch.domain.PreferencesRepository
import com.lfssolutions.retialtouch.domain.RequestState
import com.lfssolutions.retialtouch.domain.model.AppState
import com.lfssolutions.retialtouch.domain.model.basic.BasicApiRequest
import com.lfssolutions.retialtouch.domain.model.employee.EmployeeDao
import com.lfssolutions.retialtouch.domain.model.employee.EmployeesResponse
import com.lfssolutions.retialtouch.domain.model.location.LocationDao
import com.lfssolutions.retialtouch.domain.model.location.LocationResponse
import com.lfssolutions.retialtouch.domain.model.login.LoginUiState
import com.lfssolutions.retialtouch.domain.model.memberGroup.MemberGroupDao
import com.lfssolutions.retialtouch.domain.model.memberGroup.MemberGroupResponse
import com.lfssolutions.retialtouch.domain.model.members.MemberDao
import com.lfssolutions.retialtouch.domain.model.members.MemberResponse
import com.lfssolutions.retialtouch.domain.model.menu.MenuCategoriesDao
import com.lfssolutions.retialtouch.domain.model.menu.MenuCategoryResponse
import com.lfssolutions.retialtouch.domain.model.menu.MenuProductResponse
import com.lfssolutions.retialtouch.domain.model.menu.MenuProductsDao
import com.lfssolutions.retialtouch.domain.model.nextPOSSaleInvoiceNo.NextPOSSaleDao
import com.lfssolutions.retialtouch.domain.model.nextPOSSaleInvoiceNo.NextPOSSaleInvoiceNoResponse
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentTypeDao
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentTypeResponse
import com.lfssolutions.retialtouch.domain.model.posInvoice.POSInvoiceDao
import com.lfssolutions.retialtouch.domain.model.posInvoice.POSInvoiceResponse
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
import com.lfssolutions.retialtouch.domain.model.terminal.TerminalResponse
import com.lfssolutions.retialtouch.domain.repositories.NetworkRepository
import com.lfssolutions.retialtouch.utils.AppConstants.LARGE_PHONE_MAX_WIDTH
import com.lfssolutions.retialtouch.utils.AppConstants.SMALL_PHONE_MAX_WIDTH
import com.lfssolutions.retialtouch.utils.AppConstants.SMALL_TABLET_MAX_WIDTH
import com.lfssolutions.retialtouch.utils.DeviceType
import com.lfssolutions.retialtouch.utils.PrefKeys.EMPLOYEE_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.PrefKeys.EMPLOYEE_ROLE_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.PrefKeys.LOCATION_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.PrefKeys.MEMBER_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.PrefKeys.MENU_CATEGORY_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.PrefKeys.MENU_PRODUCTS_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.PrefKeys.PAYMENT_TYPE_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.PrefKeys.PRODUCT_TAX_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.PrefKeys.TERMINAL_ERROR_TITLE
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class BaseViewModel: ViewModel(), KoinComponent {

    val networkRepository: NetworkRepository by inject()
    val preferences: PreferencesRepository by inject()
    val databaseRepository: DatabaseRepository by inject()

    private val _composeAppState = MutableStateFlow(AppState())
    val composeAppState: StateFlow<AppState> = _composeAppState.asStateFlow()

    val _loginScreenState = MutableStateFlow(LoginUiState())
    val loginScreenState: StateFlow<LoginUiState> = _loginScreenState

    var count =0
    private val _authenticationDao = MutableStateFlow(AuthenticateDao())
    val authenticationDao: StateFlow<AuthenticateDao?> = _authenticationDao.asStateFlow()
    
    private val _employeeDoa = MutableStateFlow<EmployeeDao?>(null)
    val employeeDoa = _employeeDoa.asStateFlow()
    
    private val _isMenuCategoryDbInserted = MutableStateFlow(false)
    val isMenuCategoryDbInserted: StateFlow<Boolean> get() = _isMenuCategoryDbInserted

    private val _categoryMenuDao = MutableStateFlow<List<MenuCategoriesDao>>(emptyList())
    val categoryMenuDao: StateFlow<List<MenuCategoriesDao?>> = _categoryMenuDao

    private val _isMenuProductDbInserted = MutableStateFlow(false)
    val isMenuProductDbInserted: StateFlow<Boolean> get() = _isMenuProductDbInserted

    private val _productMenuDao = MutableStateFlow<List<MenuProductsDao>>(emptyList())
    val productMenuDao: StateFlow<List<MenuProductsDao?>> = _productMenuDao
    
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

    val _nextPOSSaleResponse = MutableStateFlow<NextPOSSaleInvoiceNoResponse?>(null)
    val nextPOSSaleResponse: StateFlow<NextPOSSaleInvoiceNoResponse?> get() = _nextPOSSaleResponse

    val _posInvoiceResponse = MutableStateFlow<POSInvoiceResponse?>(null)
    val _productTaxResponse = MutableStateFlow<ProductWithTaxByLocationResponse?>(null)
    val _productLocationResponse = MutableStateFlow<ProductLocationResponse?>(null)
    val _paymentTypeResponseResponse = MutableStateFlow<PaymentTypeResponse?>(null)

    val _membersResponse = MutableStateFlow<MemberResponse?>(null)
    val membersResponse: StateFlow<MemberResponse?> get() = _membersResponse

    val _memberGroupResponse = MutableStateFlow<MemberGroupResponse?>(null)
    val memberGroupResponse: StateFlow<MemberGroupResponse?> get() = _memberGroupResponse

    val _promotionResponse = MutableStateFlow<PromotionResponse?>(null)
    val promotionResponse: StateFlow<PromotionResponse?> get() = _promotionResponse

    val _productBarCodeGroupResponse = MutableStateFlow<ProductBarCodeResponse?>(null)
    val productBarCodeGroupResponse: StateFlow<ProductBarCodeResponse?> get() = _productBarCodeGroupResponse

    val _syncResponse = MutableStateFlow<SyncAllResponse?>(null)
    val syncResponse: StateFlow<SyncAllResponse?> get() = _syncResponse

    private val _isLoggedOut = MutableStateFlow(false)
    val isLoggedOut: StateFlow<Boolean> = _isLoggedOut.asStateFlow()


    // Expose the login state as a Flow<Boolean?>, with null indicating loading
    val isUserLoggedIn: StateFlow<Boolean?> = preferences.getUserLoggedIn()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(1000),
            initialValue = null
        )

    fun updateScreenMode(width: Dp){
        val deviceType = when {
            width < SMALL_PHONE_MAX_WIDTH -> DeviceType.SMALL_PHONE
            width in SMALL_PHONE_MAX_WIDTH..LARGE_PHONE_MAX_WIDTH -> DeviceType.LARGE_PHONE
            width in LARGE_PHONE_MAX_WIDTH..SMALL_TABLET_MAX_WIDTH -> DeviceType.SMALL_TABLET
            else -> DeviceType.LARGE_TABLET
        }
        _composeAppState.update { state -> state.copy(
            isTablet = deviceType == DeviceType.SMALL_TABLET || deviceType == DeviceType.LARGE_TABLET,
            screenWidth = width,
            deviceType = deviceType
            ) }
    }

    suspend fun initAuthenticationDao(): Boolean {
        return withContext(Dispatchers.IO) {
            val userID = preferences.getUserId().first()
            var isInitialized = false

            databaseRepository.selectUserByUserId(userID).collect { loginUser ->
                _authenticationDao.update { loginUser }
                //_isLoggedIn.update { loginUser.isLoggedIn }
                isInitialized = true // Set to true after updating
                return@collect // Exit the collect loop, since we only need the first emitted value
            }

            isInitialized // Return the initialization status
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

    suspend fun getProductsWithTax(){
        try {
            updateLoaderMsg("Fetching Product Tax data...")
            println("product tax calling api : ${count++}")
            networkRepository.getProductsWithTax(getBasicRequest()).collectLatest {apiResponse->
                observeProductWithTax(apiResponse)
            }
        }catch (e: Exception){
            val error="${e.message}"
            handleApiError(PRODUCT_TAX_ERROR_TITLE,error)
        }
    }

    suspend fun getMembers(){
        try {
            updateLoaderMsg("Fetching Member Data...")
            networkRepository.getMembers(getBasicTenantRequest()).collectLatest {apiResponse->
                observeMembers(apiResponse)
            }
        }catch (e: Exception){
            val error="${e.message}"
            handleApiError(MEMBER_ERROR_TITLE,error)
        }
    }


    suspend fun getPaymentTypes(){
        try {
            updateLoaderMsg("Fetching Payment Data...")
            networkRepository.getPaymentTypes(getBasicTenantRequest()).collectLatest {apiResponse->
                observePaymentType(apiResponse)
            }
        }catch (e: Exception){
            val error="${e.message}"
            handleApiError(PAYMENT_TYPE_ERROR_TITLE,error)
        }
    }

    suspend fun getMemberGroup(){
        try {
            updateLoaderMsg("Fetching Member Group...")
            networkRepository.getMemberGroup(getBasicTenantRequest()).collectLatest {apiResponse->
                observeMemberGroup(apiResponse)
            }
        }catch (e: Exception){
            val error="${e.message}"
            handleApiError(MEMBER_ERROR_TITLE,error)
        }
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

    suspend fun getMenuCategory(){
        try {
            updateLoaderMsg("Fetching menu categories data...")
            networkRepository.getMenuCategories(getBasicRequest()).collectLatest {apiResponse->
                observeCategoryMenu(apiResponse)
            }
        }catch (e: Exception){
            val error="${e.message}"
            handleApiError(MENU_CATEGORY_ERROR_TITLE,error)
        }
    }

    private fun getMenuProducts(id:Int){
        viewModelScope.launch {
            println("Menu products api : $id")
            networkRepository.getMenuProducts(getBasicRequest(id)).collectLatest {response->
                observeProductsMenu(response)
            }

        }
    }

    private fun observeCategoryMenu(apiResponse: RequestState<MenuCategoryResponse>) {
        println("menu category insertion : ${count++}")
        observeResponseNew(apiResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                if(apiData.success){
                    insertMenuCategories(apiData)
                    apiData.result.items.forEach { item->
                        getMenuProducts(item.id)
                    }
                }
            },
            onError = {
                    errorMsg ->
                handleApiError(MENU_CATEGORY_ERROR_TITLE,errorMsg)
            }
        )
    }

    private fun observeProductsMenu(apiResponse: RequestState<MenuProductResponse>) {
        println("menu products insertion : ${count++}")
        observeResponseNew(apiResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                if(apiData.success){
                    insertMenuProducts(apiData)
                }
            },
            onError = {
                    errorMsg ->
                handleApiError(MENU_PRODUCTS_ERROR_TITLE,errorMsg)
            }
        )
    }

    private suspend fun observeLocation(apiResponse: Flow<RequestState<LocationResponse>>) {
        println("location insertion : ${count++}")
        observeResponse(apiResponse,
            onLoading = { updateLoaderMsg("Fetching Location....")},
            onSuccess = { apiData -> insertLocation(apiData) },
            onError = {
                    errorMsg ->
                handleApiError(LOCATION_ERROR_TITLE,errorMsg)
            }
        )
    }


    private suspend fun observeEmployees(apiResponse: Flow<RequestState<EmployeesResponse>>) {
        println("employees insertion : ${count++}")
        observeResponse(apiResponse,
            onLoading = {  updateLoaderMsg("Fetching Employees....")},
            onSuccess = { apiData -> insertEmployees(apiData) },
            onError = {errorMsg->
                handleApiError(EMPLOYEE_ERROR_TITLE,errorMsg)
            }
        )
    }

    private suspend fun observeEmpRole(apiResponse: Flow<RequestState<EmployeesResponse>>) {
        println("employees role insertion : ${count++}")
        observeResponse(apiResponse,
            onLoading = {  updateLoaderMsg("Fetching Employees Role....")},
            onSuccess = { apiData -> insertEmpRole(apiData) },
            onError = {errorMsg->
                handleApiError(EMPLOYEE_ROLE_ERROR_TITLE,errorMsg)
            }
        )
    }

    private fun observeProductWithTax(apiResponse: RequestState<ProductWithTaxByLocationResponse>) {
        println("product tax insertion : ${count++}")
        observeResponseNew(apiResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                if(apiData.success){
                    insertProductWithTax(apiData)
                }
            },
            onError = {
                    errorMsg ->
                handleApiError(PRODUCT_TAX_ERROR_TITLE,errorMsg)
            }
        )
    }

    private fun observeMembers(apiResponse: RequestState<MemberResponse>) {
        println("member insertion : ${count++}")

        observeResponseNew(apiResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                if(apiData.success){
                    insertMembers(apiData)
                }
            },
            onError = {
                    errorMsg ->
                handleApiError(MEMBER_ERROR_TITLE,errorMsg)
            }
        )
    }

    private fun observeMemberGroup(apiResponse: RequestState<MemberGroupResponse>) {
        println("member group insertion : ${count++}")

        observeResponseNew(apiResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                if(apiData.success){
                    insertMemberGroup(apiData)
                }
            },
            onError = {
                    errorMsg ->
                handleApiError(MEMBER_ERROR_TITLE,errorMsg)
            }
        )
    }

    private fun observePaymentType(apiResponse: RequestState<PaymentTypeResponse>) {
        println("payment type insertion : ${count++}")

        observeResponseNew(apiResponse,
            onLoading = {  },
            onSuccess = { apiData ->
                if(apiData.success){
                    insertPaymentType(apiData)
                }
            },
            onError = {
                    errorMsg ->
                handleApiError(MEMBER_ERROR_TITLE,errorMsg)
            }
        )
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

    fun handleApiError(errorTitle:String,errorMsg: String) {
        viewModelScope.launch(Dispatchers.Main) {
            println(errorMsg)
            // Update login state to reflect error and stop further execution
            updateLoginState(
                loading = _loginScreenState.value.isLoading,
                successfulLogin = false,
                loginError = true,
                title = errorTitle,
                error = errorMsg)
        }
    }
    suspend fun getCurrencySymbol() : String{
        return preferences.getCurrencySymbol().first()  // Collects the first emitted value from Flow
    }

    suspend fun insertUserTenantSafely(
        loginResponse: LoginResponse,
        finalUrl: String,
        tenant: String,
        username: String,
        password: String
    ) {
        withContext(Dispatchers.IO) {
            // If no duplicate is found, insert the user
            val authenticateDao=AuthenticateDao(
                userId = loginResponse.userId,
                tenantId = loginResponse.tenantId ?: -1,
                userName = username,
                serverURL = finalUrl,
                tenantName = tenant,
                password = password,
                isLoggedIn = true,
                isSelected = true,
                loginDao = loginResponse
            )

            databaseRepository.insertAuthentication(authenticateDao)
        }
    }

    fun insertLocation(
        locationResponse: LocationResponse
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // If no duplicate is found, insert the user
                deleteExistingLocations()
                locationResponse.result.items.forEachIndexed{index,location->
                    val mLocationDao=
                        LocationDao(
                            locationId = location.id,
                            name = location.name,
                            code = location.code,
                            country = location.country,
                            address1 = location.address1,
                            address2 = location.address2,
                            isSelected = false
                        )
                    databaseRepository.insertLocation(mLocationDao)
                }
            }
        }
    }


    fun insertEmployees(
        employeesResponse: EmployeesResponse
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                employeesResponse.result.items.forEach{employee->
                    val mEmployeeDao=
                        EmployeeDao(
                            employeeId = employee.id,
                            employeeName = employee.name,
                            employeeCode = employee.employeeCode,
                            employeeRoleName = employee.employeeRoleName,
                            employeePassword = employee.password,
                            employeeCategoryName = employee.employeeCategoryName ?: "",
                            employeeDepartmentName = employee.employeeDepartmentName ?: "",
                            isAdmin = employee.isAdmin,
                            isDeleted = employee.isDeleted
                        )
                    databaseRepository.insertEmployee(mEmployeeDao)
                }
            }
        }
    }

    fun insertEmpRole(
        employeesResponse: EmployeesResponse
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                employeesResponse.result.items.forEach{employee->
                    val mEmployeeDao=
                        EmployeeDao(
                            employeeId = employee.id,
                            employeeName = employee.name,
                            isAdmin = employee.isAdmin,
                            isDeleted = employee.isDeleted
                        )
                    databaseRepository.insertEmpRole(mEmployeeDao)
                }
            }
        }
    }

    fun getEmployeeByCode(empCode:String) {
        viewModelScope.launch {
            databaseRepository.getEmployeeByCode(empCode.trim())
                .collect { employee ->
                    _employeeDoa.update { employee }
                }
        }
    }

    fun insertMenuCategories(
        response: MenuCategoryResponse
    ) {
        viewModelScope.launch {
            var isSuccess = true // Flag to track if all insertions are successful
            try {
                withContext(Dispatchers.IO) {
                    response.result.items.forEach{item->
                        val dao=
                            MenuCategoriesDao(
                                categoryId =  item.id.toLong(),
                                categoryItem = item
                            )
                        // Try inserting and handle possible failure
                        try {
                            databaseRepository.insertMenuCategories(dao)
                        } catch (e: Exception) {
                            isSuccess = false // Mark as failed if any insertion fails
                        }
                    }
                }
                updateMenuCategoriesDb(isSuccess)
            }catch (ex:Exception){
                // Once all insertions are done, notify the result
                updateMenuCategoriesDb(false)
            }
        }
    }

    fun insertMenuProducts(
        response: MenuProductResponse
    ) {
        viewModelScope.launch {
            var isSuccess = true
            try {
                withContext(Dispatchers.IO) {
                    response.result.items.forEach{item->
                        val dao=
                            MenuProductsDao(
                                productId =  item.id.toLong(),
                                menuProductItem = item
                            )
                        try {
                            databaseRepository.insertMenuProducts(dao)
                        } catch (e: Exception) {
                            isSuccess = false // Mark as failed if any insertion fails
                        }
                    }
                }
                // Once all insertions are done, notify the result
                updateMenuProductsDb(isSuccess)
            }catch (ex:Exception){
                isSuccess=false
                updateMenuProductsDb(isSuccess)
            }
        }
    }


    fun insertNextPosSale(
        response: NextPOSSaleInvoiceNoResponse
    ) {
        viewModelScope.launch {
            var isSuccess = true
            try {
                withContext(Dispatchers.IO) {
                    response.result?.let {
                        val dao= NextPOSSaleDao(
                            posItem = it
                        )

                        try {
                            databaseRepository.insertNextPosSale(dao)
                        }
                        catch (ex:Exception){
                            isSuccess=false
                        }

                    }
                }
                updateNextPOSSaleDb(isSuccess)
            }catch (ex:Exception){
               isSuccess=false
                updateNextPOSSaleDb(isSuccess)
            }
        }
    }

    fun insertPosInvoice(
        response: POSInvoiceResponse
    ) {
        viewModelScope.launch {
            var isSuccess = true
            try {
                withContext(Dispatchers.IO) {
                    response.result?.items?.forEach {item->
                        val dao= POSInvoiceDao(
                            posInvoiceId = item.id.toLong(),
                            totalCount = response.result.totalCount?.toLong()?:0,
                            posItem = item,
                        )
                        try {
                            databaseRepository.insertPosInvoice(dao)
                        }catch (ex:Exception){
                            isSuccess = false
                        }

                    }
                }

            }catch (ex:Exception){
                isSuccess = false
            }

            // If successful, invoke the callback with true
            updatePOSInvoiceDb(isSuccess)
        }
    }


    fun insertPaymentType(
        response: PaymentTypeResponse
    ) {
        viewModelScope.launch {
            var isSuccess = true
            try {
                withContext(Dispatchers.IO) {
                    response.result?.items?.forEach {item->
                        val dao= PaymentTypeDao(
                            paymentId = item.id.toLong(),
                            rowItem = item,
                        )
                        try {
                            databaseRepository.insertPaymentType(dao)
                        } catch (e: Exception) {
                            isSuccess = false // Mark as failed if any insertion fails
                        }
                    }
                }

            }catch (ex:Exception){
                isSuccess=false
            }

            // If successful, invoke the callback with true
            updatePaymentTypeDb(isSuccess)
        }
    }

    fun insertProductWithTax(
        response: ProductWithTaxByLocationResponse
    ) {
        viewModelScope.launch {
            var isSuccess = true
            try {
                withContext(Dispatchers.IO) {
                    response.result?.items?.forEach {item->
                        val dao= ProductTaxDao(
                            productTaxId = item.id.toLong(),
                            rowItem = item,
                        )
                        try {
                            databaseRepository.insertProductWithTax(dao)
                        } catch (e: Exception) {
                            isSuccess = false // Mark as failed if any insertion fails
                        }
                    }
                }

            }catch (ex:Exception){
                isSuccess=false
            }

            // If successful, invoke the callback with true
            updateProductTaxDb(isSuccess)
        }
    }

    suspend fun updateProductWithTax(updatedItem: ProductTaxItem) {
        // Switch to the IO dispatcher to perform the database operation
        withContext(Dispatchers.IO) {
            val dao = ProductTaxDao(
                productTaxId = updatedItem.id.toLong(),
                rowItem = updatedItem,
                isScanned = true
            )
            // Call the repository method to update the product
            databaseRepository.updateProductWithTax(dao)
        }
    }

    suspend fun insertScannedProduct(scannedList: List<ProductTaxItem>) {
        // Switch to the IO dispatcher to perform the database operation
        withContext(Dispatchers.IO) {
            scannedList.forEach {item->
                val dao = ScannedProductDao(
                    productId = item.id.toLong(),
                    name = item.name?:"",
                    inventoryCode = item.inventoryCode?:"",
                    barCode = item.barCode?:"",
                    qty = item.qtyOnHand,
                    price = item.price?:0.0,
                    subtotal = item.price?.times(item.qtyOnHand)?:0.0,
                    taxValue = item.taxValue?:0.0,
                    taxPercentage = item.taxPercentage?:0.0,
                    rowItem = item,
                )
                databaseRepository.insertScannedProduct(dao)
            }
        }
    }

    fun insertMembers(
        response: MemberResponse
    ) {
        viewModelScope.launch {
            var isSuccess = true
            try {
                withContext(Dispatchers.IO) {
                    response.result?.items?.forEach {item->
                        val dao= MemberDao(
                            memberId = item.id.toLong(),
                            rowItem = item,
                        )
                        try {
                            databaseRepository.insertMembers(dao)
                        } catch (e: Exception) {
                            isSuccess = false // Mark as failed if any insertion fails
                        }
                    }

                    // If successful, invoke the callback with true
                    updateMemberDb(isSuccess)
                }
            }catch (ex:Exception){
                isSuccess=false
            }
        }
    }

    fun insertMemberGroup(
        response: MemberGroupResponse
    ) {
        viewModelScope.launch {
            var isSuccess = true
            try {
                withContext(Dispatchers.IO) {
                    response.result?.items?.forEach {item->
                        val dao= MemberGroupDao(
                            memberGroupId = item.id.toLong(),
                            rowItem = item,
                        )
                        try {
                            databaseRepository.insertMemberGroup(dao)
                        } catch (e: Exception) {
                            isSuccess = false
                        }
                    }

                    // If successful, invoke the callback with true
                    updateMemberGroupDb(isSuccess)
                }
            }catch (ex:Exception){
                isSuccess=false
            }
        }
    }

    fun insertProductLocation(
        response: ProductLocationResponse
    ) {
        viewModelScope.launch {
            var isSuccess = true
            try {
                withContext(Dispatchers.IO) {
                    response.result?.items?.forEach {item->
                        val dao= ProductLocationDao(
                            productLocationId = item.id.toLong(),
                            rowItem = item,
                        )
                        try {
                            databaseRepository.insertProductLocation(dao)
                        } catch (e: Exception) {
                            isSuccess = false // Mark as failed if any insertion fails
                        }
                    }
                }

            }catch (ex:Exception){
                isSuccess=false
            }

            // If successful, invoke the callback with true
            updateProductLocationDb(isSuccess)
        }
    }


    fun insertSyncAll(
        response: SyncAllResponse
    ) {
        viewModelScope.launch {
            var isSuccess = true
            try {
                withContext(Dispatchers.IO) {
                    response.result.items.forEach {item->
                        val dao= SyncAllDao(
                            syncId = item.id.toLong(),
                            rowItem = item,
                        )
                        try {
                            databaseRepository.insertSyncAll(dao)
                        } catch (e: Exception) {
                            isSuccess = false // Mark as failed if any insertion fails
                        }
                    }
                }

            }catch (ex:Exception){
                isSuccess=false
            }

            // If successful, invoke the callback with true
            updateSyncDb(isSuccess)
        }
    }

    private fun updateMenuCategoriesDb(result:Boolean) {
        _isMenuCategoryDbInserted.update { result }
    }

    private fun updateMenuProductsDb(result:Boolean) {
        _isMenuProductDbInserted.update { result }
    }

    private fun updateNextPOSSaleDb(result:Boolean) {
        _isNEXTPOSSaleDbInserted.update { result }
    }

    private fun updatePOSInvoiceDb(result:Boolean) {
        _isPOSInvoiceDbInserted.update { result }
    }

    private fun updatePaymentTypeDb(result:Boolean) {
        _isPaymentTypeDbInserted.update { result }
    }

    private fun updateProductTaxDb(result:Boolean) {
        _isProductTaxDbInserted.update { result }
    }

    private fun updateProductLocationDb(result:Boolean) {
        _isProductLocationDbInserted.update { result }
    }
    private fun updateMemberDb(result:Boolean) {
        _isMembersDbInserted.update { result }
    }

    private fun updateMemberGroupDb(result:Boolean) {
        _isMemberGroupDbInserted.update { result }
    }

    private fun updateSyncDb(result:Boolean) {
        _isSyncDbInserted.update { result }
    }


    fun getAllProductTax() {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.getAllProductWithTax()
                .collectLatest { itemDao ->
                   // _productMenuDao.update { itemDao }
                }
        }
    }

    fun getAllMenuCategories() {
        viewModelScope.launch {
            databaseRepository.getAllCategories()
                .collect { itemDao ->
                    _categoryMenuDao.update { itemDao }
                }
        }
    }

    fun getAllMenuCProducts() {
        viewModelScope.launch {
            databaseRepository.getAllProducts()
                .collect { itemDao ->
                    _productMenuDao.update { itemDao }
                }
        }
    }

    private fun isNEXTPOSSaleDbInserted() {
        viewModelScope.launch {
            databaseRepository.getNextPosSaleCount().collect{count->
                _isNEXTPOSSaleDbInserted.update { count==0 }
            }
        }
    }
    
    private fun isMenuCategoryDbInserted() {
        viewModelScope.launch {
            databaseRepository.getMenuCategoriesCount().collect{count->
                _isMenuCategoryDbInserted.update { count>0 }
            }
        }
    }

    private fun isMenuProductDbInserted() {
        viewModelScope.launch {
            databaseRepository.getMenuCProductsCount().collect{count->
                _isMenuProductDbInserted.update { count==0 }
            }
        }
    }

    private suspend fun deleteExistingLocations() {
        databaseRepository.deleteAllLocations()
    }

    fun setUserLoggedIn(result:Boolean){
        viewModelScope.launch {
            preferences.setUserLoggedIn(result)
        }
    }

    suspend fun getBasicRequest() = BasicApiRequest(
        tenantId = preferences.getTenantId().first(),
        locationId = preferences.getLocationId().first(),
        )

     fun getBasicRequest(id:Int) = BasicApiRequest(
        id =id
    )

    suspend fun getBasicTenantRequest() = BasicApiRequest(
        tenantId = preferences.getTenantId().first()
    )

    suspend fun getLocationId() = preferences.getLocationId().first()
    suspend fun getTenantId() = preferences.getTenantId().first()


    fun resetStates() {
        viewModelScope.launch {
            val jobs = listOf(
                async { _authenticationDao.update { AuthenticateDao() } },
                async { _employeeDoa.update { EmployeeDao() } },
                async { _categoryMenuDao.update { emptyList()} },
                async { _productMenuDao.update { emptyList()} },
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
                async { databaseRepository.deleteAuthentication() },
                async { databaseRepository.deleteAllLocations() },
                async { databaseRepository.deleteAllEmployee() },
                async { databaseRepository.deleteAllEmpRole() },
                async { databaseRepository.deleteCategories() },
                async { databaseRepository.deleteProducts() },
                async { databaseRepository.deleteProductWithTax() }
            )
            jobs.awaitAll()
        }
    }
}