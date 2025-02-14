package com.hashmato.retailtouch.domain.model.login

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val password: String="",
    val tenancyName: String="",
    val usernameOrEmailAddress: String=""
)
