package com.hashmato.retailtouch.domain.model.posInvoices

import kotlinx.serialization.Serializable

@Serializable
data class PosSaleDetails(
    val id: Int = 0,
    val productId: Long = 0,
    val inventoryCode: String = "",
    val inventoryName: String = "",
    val qty: Double=0.0,
    val price: Double=0.0,
    val total: Double=0.0,
    val totalAmount: Double=0.0,
    val subTotal: Double=0.0,
    val itemDiscountPerc: Double=0.0,
    val itemDiscount: Double=0.0,
    val finalPrice: Double=0.0,
    val discount: Double=0.0,
    val tax: Double = 0.0,
    val taxPercentage: Double = 0.0,
    val posPaymentRecordId: Long = 0
)

