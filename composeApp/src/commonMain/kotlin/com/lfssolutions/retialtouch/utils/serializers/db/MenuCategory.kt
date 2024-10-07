package com.lfssolutions.retialtouch.utils.serializers.db

import com.lfssolutions.retialtouch.domain.model.menu.MenuCategoryItem
import com.lfssolutions.retialtouch.utils.JsonObj
import kotlinx.serialization.encodeToString

fun MenuCategoryItem.toJson(): String = JsonObj.encodeToString(this)

fun String.toMenuCategoryItem(): MenuCategoryItem = JsonObj.decodeFromString(this)