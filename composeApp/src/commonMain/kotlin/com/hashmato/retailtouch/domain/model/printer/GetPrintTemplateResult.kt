package com.hashmato.retailtouch.domain.model.printer

import com.hashmato.retailtouch.domain.model.ErrorResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetPrintTemplateResult(
    @SerialName("error")
    val error: ErrorResponse?,
    @SerialName("success")
    val success: Boolean,
    @SerialName("result")
    val result: List<PrinterTemplates>?= emptyList(),
)
