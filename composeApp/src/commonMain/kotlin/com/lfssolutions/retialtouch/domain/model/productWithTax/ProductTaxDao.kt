package com.lfssolutions.retialtouch.domain.model.productWithTax

import com.lfssolutions.retialtouch.domain.model.inventory.Product
import kotlinx.serialization.Serializable


@Serializable
data class ProductTaxDao(
    val productTaxId: Long = 0L,
    val isScanned :Boolean =false,
    val product: Product = Product(),
)
