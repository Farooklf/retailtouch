package com.lfssolutions.retialtouch.domain.model.products

import kotlinx.serialization.Serializable


@Serializable
data class ProductDao(
    val productId: Long = 0L,
    val isScanned :Boolean = false,
    val product: POSProduct = POSProduct(),
)
