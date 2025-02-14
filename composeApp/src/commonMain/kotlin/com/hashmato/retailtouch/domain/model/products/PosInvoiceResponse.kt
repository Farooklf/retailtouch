package com.hashmato.retailtouch.domain.model.products


import com.hashmato.retailtouch.domain.model.ErrorResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PosInvoiceResponse(
    @SerialName("__abp")
    val abp: Boolean,
    @SerialName("error")
    val error: ErrorResponse?,
    @SerialName("success")
    val success: Boolean,
    @SerialName("result")
    val result: PosResult? = null,
    @SerialName("targetUrl")
    val targetUrl: String?,
    @SerialName("unAuthorizedRequest")
    val unAuthorizedRequest: Boolean
)

@Serializable
data class PosResult(
    @SerialName("posInvoice")
    val posInvoice: Long? = 0,
    @SerialName("posInvoiceNo")
    val posInvoiceNo: String? = ""
)