package com.hashmato.retailtouch.domain.model.productLocations


import com.hashmato.retailtouch.domain.model.ErrorResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductLocationResponse(
    @SerialName("__abp")
    val abp: Boolean? = false,
    @SerialName("error")
    val error: ErrorResponse?,
    @SerialName("result")
    val result: ProductLocationResult? = ProductLocationResult(),
    @SerialName("success")
    val success: Boolean = false,
    @SerialName("unAuthorizedRequest")
    val unAuthorizedRequest: Boolean? = false
)