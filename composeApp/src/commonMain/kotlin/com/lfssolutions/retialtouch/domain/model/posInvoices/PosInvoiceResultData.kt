package com.lfssolutions.retialtouch.domain.model.posInvoices

import com.lfssolutions.retialtouch.domain.model.products.PosInvoice
import com.lfssolutions.retialtouch.domain.model.products.PosInvoiceDetail
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PosInvoiceResultData(
    @SerialName("posInvoice")
    val posInvoice: PosInvoice?=null,
    @SerialName("posInvoiceDetail")
    val posInvoiceDetail: List<PosInvoiceDetail>?= null,
)
