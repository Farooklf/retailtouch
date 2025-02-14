package com.hashmato.retailtouch.domain.model.productBarCode


import com.hashmato.retailtouch.domain.model.ErrorResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductBarCodeResponse(
    @SerialName("__abp")
    val abp: Boolean = false,
    @SerialName("error")
    val error: ErrorResponse?,
    @SerialName("result")
    val result: ProductBarCodeResult = ProductBarCodeResult(),
    @SerialName("success")
    val success: Boolean = false,
    @SerialName("targetUrl")
    val targetUrl: String? = null,
    @SerialName("unAuthorizedRequest")
    val unAuthorizedRequest: Boolean = false
)