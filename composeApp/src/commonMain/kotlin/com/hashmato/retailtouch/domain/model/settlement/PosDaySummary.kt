package com.hashmato.retailtouch.domain.model.settlement

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PosDaySummary(
    @SerialName("posDate")
    val posDate: String? = null,
    @SerialName("itemTotal")
    val itemTotal: Double? =  0.0,
    @SerialName("floatMoney")
    val floatMoney: Double? =  0.0,
    @SerialName("cashIn")
    val cashIn: Double? =  0.0,
    @SerialName("cashOut")
    val cashOut: Double? =  0.0,
    @SerialName("subTotal")
    val subTotal: Double? = 0.0,
    @SerialName("tax")
    val tax: Double? =  0.0,
    @SerialName("amount")
    val amount: Double? =  0.0,
    @SerialName("netTotal")
    val netTotal: Double? =  0.0,
    @SerialName("items")
    val items: List<PosPaymentTypeSummary>? = emptyList()
)
