package com.lfssolutions.retialtouch.domain.model.settlement

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PosPaymentSummary(
    @SerialName("locationId")
    val locationId: Long? = 0,
    @SerialName("name")
    val name: String? = "",
    @SerialName("numberofTransaction")
    val numberOfTransaction: Double? = 0.0,
    @SerialName("gstAmount")
    val gstAmount: Double? = 0.0,
    @SerialName("subTotal")
    val subTotal: Double? = 0.0,
    @SerialName("itemDates")
    val itemDates: List<PosDaySummary>? = emptyList()
)
