package com.lfssolutions.retialtouch.domain.model.products


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductWithTaxByLocationResult(
    @SerialName("items")
    val items: List<ProductItem>? = listOf()
)