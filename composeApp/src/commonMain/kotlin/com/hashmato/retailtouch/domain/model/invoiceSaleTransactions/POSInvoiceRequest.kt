package com.hashmato.retailtouch.domain.model.invoiceSaleTransactions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class POSInvoiceRequest(
    @SerialName("locationId")
    var locationId: Int?,
    @SerialName("maxResultCount")
    var maxResultCount: Int=1,
    @SerialName("skipCount")
    var skipCount: Int=0,
    @SerialName("sorting")
    var sorting: String="Id",

)
