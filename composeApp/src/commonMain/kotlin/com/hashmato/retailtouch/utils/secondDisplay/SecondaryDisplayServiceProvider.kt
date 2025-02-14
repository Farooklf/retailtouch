package com.hashmato.retailtouch.utils.secondDisplay

import com.hashmato.retailtouch.domain.model.products.CartItem

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