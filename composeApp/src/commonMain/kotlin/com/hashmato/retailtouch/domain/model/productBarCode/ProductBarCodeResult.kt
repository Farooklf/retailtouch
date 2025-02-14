package com.hashmato.retailtouch.domain.model.productBarCode

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductBarCodeResult(
    @SerialName("items")
    val items: List<Barcode> = listOf()
)