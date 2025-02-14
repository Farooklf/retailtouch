package com.hashmato.retailtouch.domain.model.paymentType


import com.hashmato.retailtouch.domain.model.ErrorResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentTypeResponse(
    @SerialName("__abp")
    val abp: Boolean? = false,
    @SerialName("error")
    val error: ErrorResponse?,
    @SerialName("result")
    val result: PaymentTypeResult? = PaymentTypeResult(),
    @SerialName("success")
    val success: Boolean = false,
    @SerialName("targetUrl")
    val targetUrl: String? = null,
    @SerialName("unAuthorizedRequest")
    val unAuthorizedRequest: Boolean? = false
)