package com.lfssolutions.retialtouch.domain.model.menu

import kotlinx.serialization.Serializable

@Serializable
data class StockCategory(
    val id: Int = 0,
    val menuId: Int = 0,
    val sortOrder: Int = 0,
    val name: String = "",
    val fgColor: String = "",
    val bgColor: String = "",
    val itemFgColor: String = "",
    val itemBgColor: String = "",
    val itemColumnCount: Int = 0,
    val categoryRowCount: Int = 0,
    val itemRowCount: Int = 0,
)
