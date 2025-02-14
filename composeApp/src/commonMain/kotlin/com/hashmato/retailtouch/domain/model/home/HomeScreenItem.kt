package com.hashmato.retailtouch.domain.model.home

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource


data class HomeScreenItem(
    val homeItemId:Int,
    val icon: DrawableResource,
    val labelResId: StringResource,
    var isSyncRotate: Boolean = false
)
