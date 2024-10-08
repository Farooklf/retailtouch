package com.lfssolutions.retialtouch.domain.model.productWithTax


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreatePOSInvoiceRequest(
    @SerialName("posInvoice")
    val posInvoice: PosInvoice = PosInvoice()
)