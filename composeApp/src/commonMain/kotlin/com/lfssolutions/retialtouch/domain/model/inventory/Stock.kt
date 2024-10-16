package com.lfssolutions.retialtouch.domain.model.inventory

import kotlinx.serialization.Serializable

@Serializable
data class Stock(
    val id: Int? = null,
    val name: String="",
    val categoryId: Int? = null,
    val productId: Int? = null,
    val sortOrder: Int? = null,
    var stockPrice: Double? = null,
    val tax: Double? = null,
    val inventoryCode: String? = null,
    val fgColor: String? = null,
    val bgColor: String? = null,
    var icon: String? = null,
    val barcode: String? = null
) {
    fun matches(searchText: String): Boolean {
        val ptrn = searchText.lowercase()
        return name.lowercase().contains(ptrn)
    }
}
