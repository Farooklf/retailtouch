package com.lfssolutions.retialtouch.domain.model.posInvoice


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class POSInvoiceResult(
    @SerialName("items")
    val items: List<POSInvoiceItem>? = listOf(),
    @SerialName("totalCount")
    val totalCount: Int? = 0
)