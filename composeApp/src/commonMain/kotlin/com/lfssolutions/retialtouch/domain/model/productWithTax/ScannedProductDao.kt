package com.lfssolutions.retialtouch.domain.model.productWithTax

import kotlinx.serialization.Serializable


@Serializable
data class ScannedProductDao(
    val productId: Long = 0L,
    val name:String="",
    val inventoryCode:String="",
    val barCode:String="",
    val qty:Double=0.0,
    val price:Double=0.0,
    val discount:Double=0.0,
    val subtotal:Double=0.0,
    val taxValue:Double=0.0,
    val taxPercentage:Double=0.0
)
