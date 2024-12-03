package com.lfssolutions.retialtouch.domain.model.invoiceSaleTransactions

import kotlinx.serialization.Serializable


@Serializable
data class POSInvoiceDao(
    val posInvoiceId: Long = 0L,
    val totalCount: Long = 0L,
    val posItem: POSInvoiceItem = POSInvoiceItem(),
)
