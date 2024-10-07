package com.lfssolutions.retialtouch.domain.model.productWithTax


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductWithTaxByLocationResult(
    @SerialName("items")
    val items: List<ProductTaxItem>? = listOf()
)