package com.lfssolutions.retialtouch.utils.serializers.db

import com.lfssolutions.retialtouch.domain.model.menu.CategoryItem
import com.lfssolutions.retialtouch.utils.JsonObj
import kotlinx.serialization.encodeToString

fun CategoryItem.toJson(): String = JsonObj.encodeToString(this)

fun String.toMenuCategoryItem(): CategoryItem = JsonObj.decodeFromString(this)