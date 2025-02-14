package com.hashmato.retailtouch.utils.serializers.db

import com.hashmato.retailtouch.domain.model.menu.StockCategory
import com.hashmato.retailtouch.utils.JsonObj
import kotlinx.serialization.encodeToString

fun StockCategory.toJson(): String = JsonObj.encodeToString(this)

fun String.toStockCategory(): StockCategory = JsonObj.decodeFromString(this)