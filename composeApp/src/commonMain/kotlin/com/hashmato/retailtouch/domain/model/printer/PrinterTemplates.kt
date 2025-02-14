package com.hashmato.retailtouch.domain.model.printer


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrinterTemplates(
    @SerialName("id")
    val id: Long?,
    @SerialName("name")
    val name: String?="",
    @SerialName("receiptTypeName")
    val receiptTypeName: String?="",
    @SerialName("template")
    val template: String?="",
    @SerialName("type")
    val type: Int?=0,
    @SerialName("group")
    val group: Boolean?=false,

    /*val mergeLines: Boolean,
    val contents: String,
    val files: String*/
)
