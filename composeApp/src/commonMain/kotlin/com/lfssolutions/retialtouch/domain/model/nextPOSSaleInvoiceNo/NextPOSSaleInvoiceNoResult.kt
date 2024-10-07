package com.lfssolutions.retialtouch.domain.model.nextPOSSaleInvoiceNo


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NextPOSSaleInvoiceNoResult(
    @SerialName("invoiceNo")
    val invoiceNo: String? = "",
    @SerialName("invoicePrefix")
    val invoicePrefix: String? = "",
    @SerialName("posLength")
    val posLength: Int? = 0
)