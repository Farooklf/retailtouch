package com.hashmato.retailtouch.domain.model.nextPOSSaleInvoiceNo

import kotlinx.serialization.Serializable

@Serializable
data class NextPOSSaleDao(
    val posId:Long =0L,
    val posItem: NextPOSSaleInvoiceNoResult = NextPOSSaleInvoiceNoResult(),
)


