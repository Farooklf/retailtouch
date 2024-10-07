package com.lfssolutions.retialtouch.domain.model.menu


import com.lfssolutions.retialtouch.domain.model.ErrorResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuProductResponse(
    @SerialName("__abp")
    val abp: Boolean,
    @SerialName("error")
    val error: ErrorResponse?,
    @SerialName("result")
    val result: com.lfssolutions.retialtouch.domain.model.menu.MenuProductResult,
    @SerialName("success")
    val success: Boolean,
    @SerialName("targetUrl")
    val targetUrl: String?,
    @SerialName("unAuthorizedRequest")
    val unAuthorizedRequest: Boolean
)