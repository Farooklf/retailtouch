package com.lfssolutions.retialtouch.domain.model.inventory

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int? = 0,
    val name: String? = null,
    val productCode: String? = null,
    val price: Double? = null,
    val tax: Double? = null,
    val qtyOnHand: Double = 0.0,
    val itemDiscount: Double = 0.0,
    val image: String = "",
    val barcode: String? = null
) {
    // Method to check if the product matches a given text
    fun matches(text: String): Boolean {
        if (text.isEmpty()) return true
        val searchText = text.lowercase()
        return (name?.lowercase()?.contains(searchText) == true ||
                productCode?.lowercase()?.contains(searchText) == true ||
                id.toString() == searchText)
    }
}

