package com.lfssolutions.retialtouch.domain.model.posInvoices

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class GetPosInvoiceForEditRequest(
    @SerialName("id")
    val id: Long,
)