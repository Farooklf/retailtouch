package com.hashmato.retailtouch.utils.secondDisplay

import com.hashmato.retailtouch.domain.model.products.CartItem

actual class SecondaryDisplayServiceProvider actual constructor() {
    actual fun updateDefaultImage(imageUrl: String) {
    }

    actual fun updateCartItems(
        cartItems: List<CartItem>,
        cartSubTotal: Double,
        cartTotal: Double,
        cartTotalTax: Double,
        cartItemTotalDiscount: Double,
        cartTotalDiscount: Double,
        currencySymbol: String
    ) {
    }

}