package com.lfssolutions.retialtouch.domain.model.menu


import com.lfssolutions.retialtouch.domain.model.ErrorResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponse(
    @SerialName("__abp")
    val abp: Boolean = false,
    @SerialName("error")
    val error: ErrorResponse?,
    @SerialName("result")
    val result: CategoryResult = CategoryResult(),
    @SerialName("success")
    val success: Boolean = false,
    @SerialName("targetUrl")
    val targetUrl: String? = null,
    @SerialName("unAuthorizedRequest")
    val unAuthorizedRequest: Boolean = false
)