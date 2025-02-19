package com.hashmato.retailtouch.domain.model.paymentType


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentTypeResult(
    @SerialName("items")
    val items: List<PaymentMethod>? = listOf()
)