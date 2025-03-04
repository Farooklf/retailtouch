package com.hashmato.retailtouch.domain.model.settlement

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetPOSPaymentSummaryRequest(
    @SerialName("locations")
    val locations: List<PosLocation>? = emptyList(),
    @SerialName("startDate")
    val startDate: String? = "",
    @SerialName("endDate")
    val endDate: String? = "",
    @SerialName("shift")
    val shift: Int? = null,
)
