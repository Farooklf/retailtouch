package com.hashmato.retailtouch.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(
    val error: ApiErrorDetails?
)

@Serializable
data class ApiErrorDetails(
    val message: String?
)
