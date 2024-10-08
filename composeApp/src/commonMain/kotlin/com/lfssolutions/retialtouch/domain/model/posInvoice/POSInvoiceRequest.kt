package com.lfssolutions.retialtouch.domain.model.posInvoice

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class POSInvoiceRequest(
    @SerialName("locationId")
    val locationId: Int?,
    @SerialName("maxResultCount")
    val maxResultCount: Int=1,
    @SerialName("skipCount")
    val skipCount: Int=0,
    @SerialName("sorting")
    val sorting: String="Id",

)
