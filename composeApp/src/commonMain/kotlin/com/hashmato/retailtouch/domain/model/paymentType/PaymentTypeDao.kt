package com.hashmato.retailtouch.domain.model.paymentType

import kotlinx.serialization.Serializable


@Serializable
data class PaymentTypeDao(
    val paymentId: Long = 0L,
    val rowItem: PaymentMethod = PaymentMethod(),
)
