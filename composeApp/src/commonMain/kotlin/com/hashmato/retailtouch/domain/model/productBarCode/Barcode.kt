package com.hashmato.retailtouch.domain.model.productBarCode

import kotlinx.serialization.Serializable

@Serializable
data class Barcode(
    val productId :Long=0,
    val code :String?=null,
    val productCode :String?=null
)
