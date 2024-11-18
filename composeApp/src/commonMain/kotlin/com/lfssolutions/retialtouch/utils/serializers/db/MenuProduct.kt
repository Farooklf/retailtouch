package com.lfssolutions.retialtouch.utils.serializers.db

import com.lfssolutions.retialtouch.domain.model.products.Stock
import com.lfssolutions.retialtouch.utils.JsonObj
import kotlinx.serialization.encodeToString

fun Stock.toJson(): String = JsonObj.encodeToString(this)

fun String.toMenuProductItem(): Stock = JsonObj.decodeFromString(this)