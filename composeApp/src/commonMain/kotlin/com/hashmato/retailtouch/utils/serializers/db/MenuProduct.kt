package com.hashmato.retailtouch.utils.serializers.db

import com.hashmato.retailtouch.domain.model.products.Stock
import com.hashmato.retailtouch.utils.JsonObj
import kotlinx.serialization.encodeToString

fun Stock.toJson(): String = JsonObj.encodeToString(this)

fun String.toMenuProductItem(): Stock = JsonObj.decodeFromString(this)