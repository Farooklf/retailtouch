package com.lfssolutions.retialtouch.domain.model.productBarCode


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductBarCodeResult(
    @SerialName("items")
    val items: List<com.lfssolutions.retialtouch.domain.model.location.LocationItem> = listOf()
)