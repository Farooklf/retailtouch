package com.hashmato.retailtouch.domain.model.products


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreatePOSInvoiceRequest(
    @SerialName("posInvoice")
    val posInvoice: PosInvoice = PosInvoice()
)