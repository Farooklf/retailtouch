package com.hashmato.retailtouch.domain.model.products


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PosPayment(
    @SerialName("amount")
    val amount: Double = 0.0,
    @SerialName("id")
    val id: Int? = 0,
    @SerialName("name")
    val name: String = "",
    @SerialName("paymentTypeId")
    var paymentTypeId: Int = 0,
    @SerialName("paymentTypeName")
    val paymentTypeName: String? = null,
    @SerialName("posInvoiceId")
    val posInvoiceId: Int = 0,

    val tenderedAmount: Double = 0.0
)