package com.hashmato.retailtouch.domain.model.posInvoices

import kotlinx.serialization.Serializable

@Serializable
data class PosSalePayment(
    val id: Int = 0,
    val posInvoiceId: Int = 0,
    val paymentTypeId: Int = 0,
    val amount: Double = 0.0,
    val posPaymentRecordId: Long = 0
)
