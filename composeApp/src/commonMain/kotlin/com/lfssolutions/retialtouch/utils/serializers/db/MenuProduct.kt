package com.lfssolutions.retialtouch.utils.serializers.db

import com.lfssolutions.retialtouch.domain.model.menu.MenuProductItem
import com.lfssolutions.retialtouch.utils.JsonObj
import kotlinx.serialization.encodeToString

fun MenuProductItem.toJson(): String = JsonObj.encodeToString(this)

fun String.toMenuProductItem(): MenuProductItem = JsonObj.decodeFromString(this)