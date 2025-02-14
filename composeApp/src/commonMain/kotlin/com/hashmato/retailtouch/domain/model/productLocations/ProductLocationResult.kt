package com.hashmato.retailtouch.domain.model.productLocations


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductLocationResult(
    @SerialName("items")
    val items: List<ProductLocationItem>? = listOf()
)