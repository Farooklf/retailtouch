package com.hashmato.retailtouch.domain.model.printer


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetPrintTemplateRequest(
    @SerialName("Type")
    val type: Long,
    @SerialName("locationId")
    val locationId: Int
)
