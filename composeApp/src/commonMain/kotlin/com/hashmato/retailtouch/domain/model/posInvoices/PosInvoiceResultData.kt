package com.hashmato.retailtouch.domain.model.posInvoices

import com.hashmato.retailtouch.domain.model.products.PosInvoice
import com.hashmato.retailtouch.domain.model.products.PosInvoiceDetail
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PosInvoiceResultData(
    @SerialName("posInvoice")
    val posInvoice: PosInvoice?=null,
    @SerialName("posInvoiceDetail")
    val posInvoiceDetail: List<PosInvoiceDetail>?= null,
)
