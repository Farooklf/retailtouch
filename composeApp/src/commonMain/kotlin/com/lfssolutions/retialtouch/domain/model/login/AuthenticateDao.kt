package com.lfssolutions.retialtouch.domain.model.login

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticateDao(
    val userId: Int=0,
    val tenantId: Int=0,
    val isLoggedIn: Boolean=false,
    val isSelected: Boolean=false,
    val userName: String = "",
    val tenantName: String = "",
    val serverURL: String = "",
    val password: String = "",
    val loginDao: LoginResponse = LoginResponse(),

)


