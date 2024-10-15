package com.lfssolutions.retialtouch.utils.serializers.db

import com.lfssolutions.retialtouch.domain.model.inventory.Stock
import com.lfssolutions.retialtouch.domain.model.menu.MenuItem
import com.lfssolutions.retialtouch.utils.JsonObj
import kotlinx.serialization.encodeToString

fun Stock.toJson(): String = JsonObj.encodeToString(this)

fun String.toMenuProductItem(): Stock = JsonObj.decodeFromString(this)