package com.hashmato.retailtouch.presentation.viewModels


import androidx.lifecycle.viewModelScope
import com.hashmato.retailtouch.domain.ApiUtils.observeResponseNew
import com.hashmato.retailtouch.domain.RequestState
import com.hashmato.retailtouch.domain.model.location.Location
import com.hashmato.retailtouch.domain.model.login.LoginRequest
import com.hashmato.retailtouch.domain.model.login.LoginResponse
import com.hashmato.retailtouch.domain.model.menu.CategoryItem
import com.hashmato.retailtouch.domain.model.menu.MenuItem
import com.hashmato.retailtouch.domain.model.products.Stock
import com.hashmato.retailtouch.domain.model.promotions.PromotionDetails
import com.hashmato.retailtouch.domain.model.promotions.PromotionItem
import com.hashmato.retailtouch.domain.model.promotions.PromotionRequest
import com.hashmato.retailtouch.utils.AppConstants.EMPLOYEE_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppConstants.EMPLOYEE_ROLE_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppConstants.INVENTORY_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppConstants.LOCATION_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppConstants.MENU_CATEGORY_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppConstants.MENU_PRODUCTS_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppConstants.PAYMENT_TYPE_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppConstants.PROMOTIONS_ERROR_TITLE
import com.hashmato.retailtouch.utils.AppConstants.SYNC_TEMPLATE_ERROR_TITLE
import com.hashmato.retailtouch.utils.TemplateType
import com.hashmato.retailtouch.utils.serializers.db.parsePriceBreakPromotionAttributes
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
            reSyncOnLogin(location)
        }
    }
}
