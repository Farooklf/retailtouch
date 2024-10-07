package com.lfssolutions.retialtouch.domain.model.login



data class LoginUiState(
    val isLoading: Boolean = false,
    val loadingMessage: String = "Logging in...",

    val username: String = "admin",
    val password: String = "",
    val locationId: String = "",
    val tenant: String = "fb",
    val server: String = "http://develop.rtlconnect.net",

    val serverError: String? = null,
    val tenantError: String? = null,
    val userNameError: String? = null,
    val passwordError: String? = null,

    val isLoginError: Boolean = false,
    val loginErrorMessage: String = "",
    val loginErrorTitle: String = "",


    val userId:Int=0,
    val tenantId:Int=0,
    val token:String="",
    val isSuccessfulLogin:Boolean=false,
)
