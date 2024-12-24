package com.lfssolutions.retialtouch.domain.model

import kotlinx.serialization.Serializable

@Serializable
sealed class ApiLoaderStateResponse {
    @Serializable
    data object Success : ApiLoaderStateResponse()
    data object Loader : ApiLoaderStateResponse()
    @Serializable
    data class Error(val errorMsg: String) : ApiLoaderStateResponse()
}