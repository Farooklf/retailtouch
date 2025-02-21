package com.hashmato.retailtouch.domain.model.printer


import kotlinx.serialization.Serializable

@Serializable
data class PrintReceiptTemplate(
    val id:Long = 0,
    val type:Long = 0,
    val name: String,
    val receiptTypeName: String,
    val template: String
)
