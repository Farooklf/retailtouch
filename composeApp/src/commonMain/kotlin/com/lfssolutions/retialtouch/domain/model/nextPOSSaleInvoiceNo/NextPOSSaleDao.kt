package com.lfssolutions.retialtouch.domain.model.nextPOSSaleInvoiceNo

import kotlinx.serialization.Serializable

@Serializable
data class NextPOSSaleDao(
    val posId:Long =0L,
    val posItem: NextPOSSaleInvoiceNoResult = NextPOSSaleInvoiceNoResult(),
)


