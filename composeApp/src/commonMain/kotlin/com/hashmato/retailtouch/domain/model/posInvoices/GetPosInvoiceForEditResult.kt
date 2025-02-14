package com.hashmato.retailtouch.domain.model.posInvoices

import com.hashmato.retailtouch.domain.model.ErrorResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetPosInvoiceForEditResult(
    @SerialName("error")
    val error: ErrorResponse?,
    @SerialName("success")
    val success: Boolean,
    @SerialName("result")
    val result: PosInvoiceResultData?=null,
)
