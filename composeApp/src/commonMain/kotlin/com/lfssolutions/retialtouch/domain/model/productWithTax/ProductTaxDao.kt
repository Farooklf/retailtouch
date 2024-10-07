package com.lfssolutions.retialtouch.domain.model.productWithTax

import kotlinx.serialization.Serializable


@Serializable
data class ProductTaxDao(
    val productTaxId: Long = 0L,
    val isScanned :Boolean =false,
    val rowItem: ProductTaxItem = ProductTaxItem(),
)
