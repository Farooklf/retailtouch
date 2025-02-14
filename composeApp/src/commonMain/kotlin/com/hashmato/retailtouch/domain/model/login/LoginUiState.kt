package com.hashmato.retailtouch.domain.model.login



data class LoginUiState(
    val isLoading: Boolean = false,
    val loadingMessage: String = "Logging in...",

    val username: String = "",
    val password: String = "",
    val location: String = "",
    val tenant: String = "",
    val server: String = "http://",

    val serverError: String? = null,
    val tenantError: String? = null,
    val userNameError: String? = null,
    val passwordError: String? = null,

    val isLoginError: Boolean = false,
    val loginErrorMessage: String = "",
    val loginErrorTitle: String = "",
    val lastLocationName: String = "",


    val userId:Int=0,
    val tenantId:Int=0,
    val token:String="",
    val isSuccessfulLogin:Boolean=false,
)
