package com.lfssolutions.retialtouch.domain.model.products

import kotlinx.serialization.Serializable

@Serializable
data class Stock(
    val id: Long = 0,
    val name: String="",
    val categoryId: Int = 0,
    val productId: Long = 0,
    val sortOrder: Int = 0,
    var price: Double = 0.0,
    var tax: Double = 0.0,
    val inventoryCode: String = "",
    val fgColor: String = "",
    val bgColor: String = "",
    val imagePath: String = "",
    val barcode: String = ""
) {
    fun matches(searchText: String): Boolean {
        val ptrn = searchText.lowercase()
        return name.lowercase().contains(ptrn)
    }
}
