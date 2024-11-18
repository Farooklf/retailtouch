package com.lfssolutions.retialtouch.domain.model.sales

import kotlinx.serialization.Serializable

@Serializable
data class SaleRecord(
    val id: Long? = 0,
    val count: Long? = 0,
    val receiptNumber: String? = "",
    val amount: Double? = 0.0,
    val date: String? = "",
    val creationDate: String? = "",
    val remarks: String? = "",
    val memberId: Int? = 0,
    val deliveryDate: String? = "",
    val delivery: Boolean? = false,
    val delivered: Boolean? = false,
    val rental: Boolean? = false,
    val rentalCollected: Boolean? = false,
    val type: Int? = 0,
    val status: Int? = 0,
    val selfCollection: Boolean? = false
)

