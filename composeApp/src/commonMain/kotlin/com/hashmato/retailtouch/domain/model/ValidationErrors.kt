package com.hashmato.retailtouch.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ValidationErrors(
    val members: List<String>?,
    val message: String
)