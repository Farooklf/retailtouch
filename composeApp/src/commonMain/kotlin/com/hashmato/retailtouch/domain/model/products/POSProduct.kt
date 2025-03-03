package com.hashmato.retailtouch.domain.model.products

import kotlinx.serialization.Serializable

@Serializable
data class POSProduct(
    val id: Long = 0,
    val name: String = "",
    val productCode: String = "",
    val price: Double = 0.0,
    val tax: Double = 0.0,
    val qtyOnHand: Double = 0.0,
    val itemDiscount: Double = 0.0,
    val image: String = "",
    val barcode: String = ""
) {
    // Method to check if the product matches a given text
    fun matches(text: String): Boolean {
        if (text.isEmpty()) return true
        val searchText = text.lowercase()
        return (name.lowercase().contains(searchText)  ||
                productCode.lowercase().contains(searchText) ||
                barcode.lowercase().contains(searchText) ||
                id.toString() == searchText
                )
    }
}

