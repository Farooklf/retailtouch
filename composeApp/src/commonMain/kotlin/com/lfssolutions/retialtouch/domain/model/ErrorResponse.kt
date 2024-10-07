package com.lfssolutions.retialtouch.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val code: Int,
    val details: String,
    val message: String,
    val validationErrors: List<ValidationErrors>?
)