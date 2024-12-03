package com.lfssolutions.retialtouch.domain.model.invoiceSaleTransactions


import com.lfssolutions.retialtouch.domain.model.products.PosInvoice
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetPosInvoiceData(
    @SerialName("items")
    val items: List<PosInvoice>? = listOf(),
    @SerialName("totalCount")
    val totalCount: Long? = 0
)