package com.hashmato.retailtouch.domain.model.invoiceSaleTransactions


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetPosInvoiceData(
    @SerialName("items")
    val items: List<SaleInvoiceItem>? = listOf(),
    @SerialName("totalCount")
    val totalCount: Long? = 0
)