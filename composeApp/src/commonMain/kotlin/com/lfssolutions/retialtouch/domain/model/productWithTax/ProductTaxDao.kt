package com.lfssolutions.retialtouch.domain.model.productWithTax

import kotlinx.serialization.Serializable


@Serializable
data class ProductTaxDao(
    val productTaxId: Long = 0L,
    val isScanned :Boolean =false,
    val name :String? ="",
    val productCode :String? ="",
    val barcode :String? ="",
    val image :String? ="",
    val tax :Double? =0.0,
    val qtyOnHand :Double? =0.0,
    val price :Double? =0.0,
    val rowItem: ProductTaxItem = ProductTaxItem(),
)
