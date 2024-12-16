package com.lfssolutions.retialtouch.utils.serializers.db

import com.lfssolutions.retialtouch.domain.model.menu.CategoryItem
import com.lfssolutions.retialtouch.domain.model.menu.StockCategory
import com.lfssolutions.retialtouch.utils.JsonObj
import kotlinx.serialization.encodeToString

fun StockCategory.toJson(): String = JsonObj.encodeToString(this)

fun String.toStockCategory(): StockCategory = JsonObj.decodeFromString(this)