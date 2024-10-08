package com.lfssolutions.retialtouch.domain.model.productWithTax


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
    val paymentTypeId: Int = 0,
    @SerialName("paymentTypeName")
    val paymentTypeName: String? = null,
    @SerialName("posInvoiceId")
    val posInvoiceId: Int = 0
)