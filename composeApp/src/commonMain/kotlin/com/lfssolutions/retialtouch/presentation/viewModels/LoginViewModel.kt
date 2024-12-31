package com.lfssolutions.retialtouch.presentation.viewModels


import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.domain.ApiUtils.observeResponseNew
import com.lfssolutions.retialtouch.domain.RequestState
import com.lfssolutions.retialtouch.domain.model.location.Location
import com.lfssolutions.retialtouch.domain.model.login.LoginRequest
import com.lfssolutions.retialtouch.domain.model.login.LoginResponse
import com.lfssolutions.retialtouch.domain.model.menu.CategoryItem
import com.lfssolutions.retialtouch.domain.model.menu.MenuItem
import com.lfssolutions.retialtouch.domain.model.products.Stock
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionDetails
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionItem
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionRequest
import com.lfssolutions.retialtouch.utils.AppConstants.EMPLOYEE_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.EMPLOYEE_ROLE_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.INVENTORY_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.LOCATION_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.MEMBER_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.MENU_CATEGORY_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.MENU_PRODUCTS_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.PAYMENT_TYPE_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.AppConstants.PROMOTION
import com.lfssolutions.retialtouch.utils.AppConstants.PROMOTIONS_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.serializers.db.parsePriceBreakPromotionAttributes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent


class LoginViewModel : BaseViewModel(), KoinComponent {



    private fun updateLoader(value: Boolean) {
        viewModelScope.launch {
            _loginScreenState.update { it.copy(isLoading = value) }
        }

    }

    fun backToLogin() {
        viewModelScope.launch {
            _loginScreenState.value = _loginScreenState.value.copy(
                isLoginError = false,
                loginErrorMessage = "",
                isLoading = false
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

    fun updateServer(urlInput: String) {
        _loginScreenState.update { it.copy(server = urlInput) }
    }

    fun updateTenant(tenantInput: String) {
        _loginScreenState.update { it.copy(tenant = tenantInput) }
    }

    fun updateUsername(userInput: String) {
        _loginScreenState.update { it.copy(username = userInput) }
    }

    fun updatePassword(passwordInput: String) {
        _loginScreenState.update { it.copy(password = passwordInput) }
    }

    fun updateLocation(location: String) {
        _loginScreenState.update { it.copy(location = location) }
    }

    fun dismissErrorDialog() {
        _loginScreenState.update {it.copy(isLoginError = false, loginErrorMessage = "")}
    }


    fun onLoginClick() {
        viewModelScope.launch(Dispatchers.IO) {
            val errors = validateInputs()
            if (errors.isEmpty()) {
                // Proceed with login
                //_loginApiResponse.value= ApiState.Loading
                with(loginScreenState.value) {
                    if (server.isBlank()) {
                        updateServer("http://")
                    }
                    val finalUrl = if (server.contains("http://") || server.contains("https://")) {
                        server
                    } else {
                        "http://${server}"
                    }

                    preferences.setBaseURL(finalUrl)
                }
                performLogin()
            } else {
                _loginScreenState.value = _loginScreenState.value.copy(
                    serverError = errors["serverUrl"],
                    tenantError = errors["tenant"],
                    userNameError = errors["username"],
                    passwordError = errors["password"]
                )
            }
        }
    }

    private fun validateInputs(): Map<String, String?> {
        val errors = mutableMapOf<String, String?>()

        if (_loginScreenState.value.server.isBlank()) {
            errors["serverUrl"] = "Specify server address"
        }

        if (_loginScreenState.value.tenant.isBlank()) {
            errors["tenant"] = "Tenant name is required"
        }

        if (_loginScreenState.value.username.isBlank()) {
            errors["username"] = "Username or e-mail is required"
        }

        if (_loginScreenState.value.password.isBlank()) {
            errors["password"] = "Please type your password"
        }


        return errors
    }

    private fun performLogin() {
        viewModelScope.launch {
            try {
                    with(loginScreenState.value) {
                       // val lastLocationName = getLocationName()
                       // _loginScreenState.update { it.copy(lastLocationName=lastLocationName) }
                        networkRepository.hitLoginAPI(
                            LoginRequest(
                                usernameOrEmailAddress = username.trim(),
                                password = password.trim(),
                                tenancyName = tenant.trim()
                            )
                        ).collect{ state->
                            when(state){
                                is RequestState.Idle -> {

                                }
                                is RequestState.Loading -> {
                                    updateLoader(true)
                                    updateLoginSyncStatus("Logging In ......")
                                }
                                is RequestState.Success -> {
                                    val response = state.getSuccessData()
                                    storeLoginInfo(response)
                                }
                                is RequestState.Error -> {
                                    val errorMessage = state.getErrorMessage()
                                    println("errorMessage : $errorMessage")
                                    updateLoginState(
                                        loading = false,
                                        loginError = true,
                                        successfulLogin = false,
                                        error = errorMessage,
                                        title = ""
                                    )
                                }
                            }
                        }
                    }
            } catch (e: Exception) {
                val errorMessage = e.message.toString()
                println("errorMessage : $errorMessage")
                updateLoginState(
                    loading = false,
                    loginError = true,
                    successfulLogin = false,
                    error = errorMessage,
                    title = ""
                )
            }
        }
    }

    private fun storeLoginInfo(loginResponse: LoginResponse?) {
        loginResponse?.let {
            with(loginScreenState.value) {
                setUserDetailsToDB(
                    loginResponse = loginResponse,
                    username = username,
                    url = server,
                    password = password,
                    tenant = tenant,
                    location = location,
                )
            }
        }
    }

    private fun setUserDetailsToDB(
        loginResponse: LoginResponse,
        username: String,
        url: String,
        password: String,
        tenant: String,
        location: String
    ) {
        val finalUrl = if (url.startsWith("http://") || url.startsWith("https://")) {
            url
        } else {
            "http://${url}"
        }

        viewModelScope.launch(Dispatchers.IO) {
            dataBaseRepository.insertUser(loginResponse,finalUrl,tenant,username,password)
            preferences.setBaseURL(finalUrl)
            preferences.setToken(loginResponse.result?:"")
            preferences.setUserId(loginResponse.userId.toLong())
            preferences.setUserName(username)
            preferences.setUserPass(password)
            preferences.setTenancyName(tenant)
            preferences.setTenantId(loginResponse.tenantId?:0)
            preferences.setCurrencySymbol(loginResponse.currencySymbol?:"")
            syncLocation(location)
        }
    }

    private suspend fun syncLocation(location: String) {
        try {
            println("Syncing Location ${count++}")
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
                            syncEmployees()
                        }
                    },
                    onError = { errorMsg ->
                        updateLoginError(LOCATION_ERROR_TITLE,errorMsg)
                    }
                )
            }
        }catch (e:Exception){
            updateLoginError(LOCATION_ERROR_TITLE,e.message.toString())
        }
    }

    private suspend fun syncEmployees(){
        try {
            println("Syncing Employees : ${count++}")
            updateLoginSyncStatus("Syncing Employees...")
            networkRepository.getEmployees(getBasicRequest()).collect{apiResponse->
                observeResponseNew(apiResponse,
                    onLoading = {
                        updateLoginSyncStatus("Syncing Employees...")
                    },
                    onSuccess = { apiData ->
                        viewModelScope.launch {
                            dataBaseRepository.insertEmployees(apiData)
                            syncEmployeeRole()
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
            println("Syncing Employees Role : ${count++}")
            networkRepository.getEmployeeRole(getBasicRequest()).collect{apiResponse->
                observeResponseNew(apiResponse,
                    onLoading = {  updateLoginSyncStatus("Syncing Employees Role...")},
                    onSuccess = { apiData ->
                        viewModelScope.launch {
                            dataBaseRepository.insertEmpRole(apiData)
                            syncCategory()
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

    private suspend fun syncCategory(){
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
                                        apiData.result.items.forEach {category->
                                            println("category: $category")
                                            println("menu api for : ${category.id}")
                                            categoryList.add(category)
                                            //syncMenu(category)
                                        }
                                        dataBaseRepository.insertCategories(apiData)
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
                async {
                    val newStock : MutableList<MenuItem> = mutableListOf()
                    categoryList.forEach { cat->
                        updateLoginSyncStatus("Syncing Product Menu...")
                        println("Syncing Product Menu : ${count++}")
                        println("menu api for : ${cat.id}")
                        networkRepository.getMenuProducts(getBasicRequest(cat.id?:0)).collect {response->
                            observeResponseNew(response,
                                onLoading = {  },
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
                }.await()
                syncInventory()
            }
        }catch (e: Exception){
            val error="${e.message}"
            updateLoginError(MENU_CATEGORY_ERROR_TITLE,error)
        }
    }

    private fun syncInventory(lastSyncTime:String?=null){
        try {
            updateLoginSyncStatus("Syncing Inventory...")
             println("Syncing Inventory : ${count++}")
             viewModelScope.launch {
                 val stockQtyMap: MutableMap<Int, Double?> = mutableMapOf()
                 async { networkRepository.getProductLocation(getBasicRequest()).collectLatest {stockAvailResponse->
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
                             updateLoginError(INVENTORY_ERROR_TITLE, errorMsg)

                         }
                     )
                 } }.await()
                 println("StocksQty:$stockQtyMap")
                 async {
                     networkRepository.getProductsWithTax(getBasicRequest()).collect{inventoryResponse->
                         observeResponseNew(inventoryResponse,
                             onLoading = {  },
                             onSuccess = { apiData ->
                                 if(apiData.success){
                                     viewModelScope.launch {
                                         dataBaseRepository.insertUpdateInventory(apiData,lastSyncTime, stockQtyMap)
                                     }
                                 }
                             },
                             onError = {
                                     errorMsg ->
                                 updateLoginError(INVENTORY_ERROR_TITLE,errorMsg)
                             }
                         )
                     }
                 }.await()
                 async {
                     networkRepository.getProductBarCode(getBasicRequest()).collect{barcodesResponse->
                         observeResponseNew(barcodesResponse,
                             onLoading = {  },
                             onSuccess = { apiData ->
                                 viewModelScope.launch {
                                     dataBaseRepository.insertUpdateBarcode(apiResponse = apiData,lastSyncDateTime=lastSyncTime)
                                 }
                             },
                             onError = { errorMsg ->
                                 updateLoginError(INVENTORY_ERROR_TITLE,errorMsg)
                             }
                         )
                     }
                 }.await()

                 syncPromotion()
             }

        }catch (e: Exception){
            val error="${e.message}"
            updateLoginError(INVENTORY_ERROR_TITLE,error)
        }
    }

    private suspend fun syncPromotion(){
        try {
            viewModelScope.launch {
                val promotionList: MutableList<PromotionItem> = mutableListOf()
                async {
                    updateLoginSyncStatus("Syncing Promotions...")
                    println("Syncing Promotions : ${count++}")
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
                                    }
                                }
                            },
                            onError = {
                                    errorMsg ->
                                updateLoginError(PROMOTIONS_ERROR_TITLE,errorMsg)
                            }
                        )
                    }

                }.await()
                println("promotionList :$promotionList")
                syncPaymentTypes()
            }
        }catch (e: Exception){
            val error="${e.message}"
            updateLoginError(PROMOTIONS_ERROR_TITLE,error)
        }
    }

    private suspend fun syncPaymentTypes(){
        try {
            updateLoginSyncStatus("Syncing Payment Type...")
            println("Syncing Payment Type : ${count++}")
            networkRepository.getPaymentTypes(getBasicTenantRequest()).collect {apiResponse->
                observeResponseNew(apiResponse,
                    onLoading = {  },
                    onSuccess = { apiData ->
                        if(apiData.success){
                            viewModelScope.launch {
                                println("login api call has been finished")
                                dataBaseRepository.insertPaymentType(apiData)
                                if(!_loginScreenState.value.isLoginError){
                                    moveToNextScreen()
                                }
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

    private suspend fun syncMenu(category: CategoryItem) {
        updateLoginSyncStatus("Syncing Product Menu...")
        println("Syncing Product Menu : ${count++}")
        networkRepository.getMenuProducts(getBasicRequest(category.id?:0)).collect {response->
            observeResponseNew(response,
                onLoading = {  },
                onSuccess = { menuData ->
                    if(menuData.success){
                        val updatedStocks=menuData.result.items.map{menu->
                            Stock(
                                id = menu.id?:menu.productId?:-1,
                                name = menu.name?:"",
                                imagePath = menu.imagePath ?: "",
                                categoryId = category.id?:menu.menuCategoryId?:-1,
                                productId = menu.productId?:0,
                                sortOrder = menu.sortOrder?:0,
                                inventoryCode = menu.inventoryCode?:"",
                                fgColor = menu.foreColor ?: "",
                                bgColor = menu.backColor ?: "",
                                barcode = menu.barCode?:""
                            )
                        }
                        viewModelScope.launch { dataBaseRepository.insertNewStock(updatedStocks) }

                    }
                },
                onError = {
                        errorMsg ->
                    updateLoginError(MENU_PRODUCTS_ERROR_TITLE,errorMsg)
                }
            )
        }
    }

    fun reSyncOnLogin() {
        viewModelScope.launch(Dispatchers.IO) {

            //async {_syncEmployees() }.await()
            //async {_syncEmployeeRole() }.await()

            println("login api call has been finished")

            viewModelScope.launch(Dispatchers.Main){
                if(!_loginScreenState.value.isLoginError){
                    moveToNextScreen()
                }
            }
        }
    }
}
