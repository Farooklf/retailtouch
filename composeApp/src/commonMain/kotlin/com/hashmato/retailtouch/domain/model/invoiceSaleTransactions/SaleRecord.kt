package com.hashmato.retailtouch.domain.model.invoiceSaleTransactions

import com.hashmato.retailtouch.utils.DateTimeUtils.getCurrentLocalDate
import com.hashmato.retailtouch.utils.DateTimeUtils.getCurrentLocalDateTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class SaleRecord(
    val id: Long? = 0,
    val count: Long? = 0,
    val receiptNumber: String = "",
    val amount: Double? = 0.0,
    val date: LocalDate = getCurrentLocalDate(),
    val creationDate: LocalDateTime = getCurrentLocalDateTime(),
    val remarks: String? = "",
    val memberId: Long = 0,
    val memberName: String? = "",
    val deliveryDate: String? = "",
    val delivery: Boolean? = false,
    val delivered: Boolean? = false,
    val rental: Boolean? = false,
    val rentalCollected: Boolean? = false,
    val type: Int = 0,
    val status: Int = 0,
    val selfCollection: Boolean? = false,
    val items: SaleInvoiceItem? = SaleInvoiceItem()
)

