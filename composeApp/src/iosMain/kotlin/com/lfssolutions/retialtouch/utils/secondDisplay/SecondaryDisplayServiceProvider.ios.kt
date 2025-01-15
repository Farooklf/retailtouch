package com.lfssolutions.retialtouch.utils.secondDisplay

import com.lfssolutions.retialtouch.domain.model.products.CartItem

actual class SecondaryDisplayServiceProvider actual constructor() {
    actual fun updateDefaultImage(imageUrl: String) {
    }

    actual fun updateCartItems(
        cartItems: List<CartItem>,
        cartSubTotal: Double,
        cartTotal: Double,
        cartTotalTax: Double,
        cartTotalDiscount: Double,
        currencySymbol: String
    ) {
    }

}