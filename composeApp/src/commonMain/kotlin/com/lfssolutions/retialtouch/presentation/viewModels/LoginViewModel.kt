package com.lfssolutions.retialtouch.presentation.viewModels


import androidx.lifecycle.viewModelScope
import com.lfssolutions.retialtouch.domain.ApiUtils.observeResponse
import com.lfssolutions.retialtouch.domain.ApiUtils.observeResponseNew
import com.lfssolutions.retialtouch.domain.repositories.NetworkRepository
import com.lfssolutions.retialtouch.domain.RequestState
import com.lfssolutions.retialtouch.domain.model.employee.EmployeesResponse
import com.lfssolutions.retialtouch.domain.model.location.LocationResponse
import com.lfssolutions.retialtouch.domain.model.login.LoginRequest
import com.lfssolutions.retialtouch.domain.model.login.LoginResponse
import com.lfssolutions.retialtouch.domain.model.login.LoginUiState
import com.lfssolutions.retialtouch.domain.model.memberGroup.MemberGroupResponse
import com.lfssolutions.retialtouch.domain.model.members.MemberResponse
import com.lfssolutions.retialtouch.domain.model.menu.MenuCategoryResponse
import com.lfssolutions.retialtouch.domain.model.menu.MenuProductResponse
import com.lfssolutions.retialtouch.domain.model.productWithTax.ProductWithTaxByLocationResponse
import com.lfssolutions.retialtouch.domain.model.terminal.TerminalResponse
import com.lfssolutions.retialtouch.utils.PrefKeys.EMPLOYEE_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.PrefKeys.EMPLOYEE_ROLE_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.PrefKeys.LOCATION_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.PrefKeys.MEMBER_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.PrefKeys.MENU_CATEGORY_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.PrefKeys.MENU_PRODUCTS_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.PrefKeys.PRODUCT_TAX_ERROR_TITLE
import com.lfssolutions.retialtouch.utils.PrefKeys.TERMINAL_ERROR_TITLE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


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
            insertUserTenantSafely(loginResponse,finalUrl,tenant,username,password)
            preferences.setBaseURL(finalUrl)
            preferences.setToken(loginResponse.result?:"")
            preferences.setUserId(loginResponse.userId.toLong())
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
                //product Tax API
                async {
                    getProductsWithTax()
                },

                //Get Members
                async {
                    getMembers()
                },

                //Get Member group
                async {
                    getMemberGroup()
                },
                //Get Payment
                async {
                    getPaymentTypes()
                },
                //MenuCategory API
                async {
                    getMenuCategory()
                },

                //Terminal Api
                async {
                    getTerminal()
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

/*fun hitApiCalls() {
        viewModelScope.launch(Dispatchers.IO) {
            /* async(Dispatchers.IO) {
                                       networkRepository.getTerminal(getBasicRequest()).collectLatest{terminalResponse->
                                           observeTerminal(terminalResponse)
                                       }
                                   }.await() // Await the result of terminalResponse
                                   //observeTerminal(terminalResponse)*/
            // Sequential API calls with error handling
            val result = runCatching {
                // Location API
                updateLoaderMsg("Fetching location data...")
                println("location calling api : ${count++}")
                val loginApiResponse = networkRepository.getLocationForUser(getBasicRequest())
                observeLocation(loginApiResponse)

                // Terminal API
                updateLoaderMsg("Fetching terminal data...")
                println("terminal calling api : ${count++}")
                val terminalResponse = networkRepository.getTerminal(getBasicRequest())
                //observeTerminal(terminalResponse)

                // Employee API
                updateLoaderMsg("Fetching employees data...")
                println("employees calling api : ${count++}")
                val employeesResponse = networkRepository.getEmployees(getBasicRequest())
                observeEmployees(employeesResponse)

                // Employee Role API
                updateLoaderMsg("Fetching employee role data...")
                println("employees role calling api : ${count++}")
                val empRoleResponse = networkRepository.getEmployeeRole(getBasicRequest())
                observeEmpRole(empRoleResponse)
            }

            // Handling success and failure
            result.onSuccess {
                // All APIs successful, update the login state
                withContext(Dispatchers.Main) {
                    updateLoginState(
                        loading = false,
                        loginError = false,
                        successfulLogin = true,
                        error = "",
                        title = ""
                    )
                }
                println("apiCall is successful")
            }.onFailure { e ->
                // If any API fails, show error and return to login
                withContext(Dispatchers.Main) {
                    updateLoginState(
                        loading = false,
                        loginError = true,
                        successfulLogin = false,
                        error = "Failed: ${e.message}",
                        title = ""
                    )
                }
                println("API call failed: ${e.message}")
            }
        }
    }*/
