package com.hashmato.retailtouch.domain.model.productLocations

import kotlinx.serialization.Serializable


@Serializable
data class ProductLocationDao(
    val productLocationId: Long = 0L,
    val rowItem: ProductLocationItem = ProductLocationItem(),
)
