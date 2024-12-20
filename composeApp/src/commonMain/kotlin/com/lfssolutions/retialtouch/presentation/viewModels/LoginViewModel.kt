package com.lfssolutions.retialtouch.presentation.viewModels


import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.domain.RequestState
import com.lfssolutions.retialtouch.domain.model.login.LoginRequest
import com.lfssolutions.retialtouch.domain.model.login.LoginResponse
import com.lfssolutions.retialtouch.utils.TemplateType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

    fun updateLocationId(urlInput: String) {
        _loginScreenState.update { it.copy(locationId = urlInput) }
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
                        networkRepository.hitLoginAPI(
                            LoginRequest(
                                usernameOrEmailAddress = username.trim(),
                                password = password.trim(),
                                tenancyName = tenant.trim()
                            )
                        ).collect{ state->
                            when(state){
                                is RequestState.Idle -> {}
                                is RequestState.Loading -> {
                                    updateLoader(true)
                                    updateLoaderMsg("Logging In ......")
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
            with(_loginScreenState.value) {
                setUserDetailsToDB(
                    loginResponse = loginResponse,
                    username = username,
                    url = server,
                    password = password,
                    tenant = tenant
                )
            }
        }
    }


    private fun setUserDetailsToDB(
        loginResponse: LoginResponse,
        username: String,
        url: String,
        password: String,
        tenant: String
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
            preferences.setLocationId(loginResponse.defaultLocationId?:0)
            preferences.setCurrencySymbol(loginResponse.currencySymbol?:"")
            hitApiCall()
        }
    }

    private fun hitApiCall(){
        viewModelScope.launch(Dispatchers.IO) {
            // Prepare all API calls in parallel using async
            val deferredResults = listOf(
                //Location API
                async {
                   getLocations()
                },
                //Employee API
                async {
                   getEmployees()
                },
                //Employee Role API
                async {
                    getEmployeeRole()
                },

                async {
                    syncPrintTemplate(TemplateType.POSInvoice)
                }
            )

            // Await all tasks (this will wait for all the parallel jobs to complete)
            deferredResults.awaitAll()
            viewModelScope.launch(Dispatchers.Main){
                if(!_loginScreenState.value.isLoginError){
                    moveToNextScreen()
                }
            }
            println("apiCall is end")
        }
    }

}

