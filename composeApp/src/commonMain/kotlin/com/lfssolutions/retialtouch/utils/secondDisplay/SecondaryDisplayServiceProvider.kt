package com.lfssolutions.retialtouch.utils.secondDisplay

import com.lfssolutions.retialtouch.domain.model.products.CartItem

expect class SecondaryDisplayServiceProvider() {

    fun updateDefaultImage(imageUrl: String)

    fun updateCartItems(
        cartItems: List<CartItem>,
        cartSubTotal: Double,
        cartTotal: Double,
        cartTotalTax: Double,
        cartTotalDiscount: Double,
        currencySymbol: String
    )
}