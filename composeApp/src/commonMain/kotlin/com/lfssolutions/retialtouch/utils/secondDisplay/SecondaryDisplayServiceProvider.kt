package com.lfssolutions.retialtouch.utils.secondDisplay

import com.lfssolutions.retialtouch.domain.model.products.CartItem

expect class SecondaryDisplayServiceProvider() {

    fun updateDefaultImage(imageUrl: String)

    fun updateCartItems(
        cartItems: List<CartItem>,
        cartTotalQty: Double,
        cartSubTotal: Double,
        cartTotal: Double,
        cartTotalTax: Double,
        cartItemTotalDiscount: Double,
        cartNetDiscounts: Double,
        currencySymbol: String
    )
}