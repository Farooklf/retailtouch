package com.hashmato.retailtouch.utils.secondDisplay

import com.hashmato.retailtouch.ShareDisplayObject
import com.hashmato.retailtouch.domain.model.products.CartItem

actual class SecondaryDisplayServiceProvider actual constructor() {
    private val secondaryDisplay = ShareDisplayObject.secondaryDisplayInstance

    actual fun updateDefaultImage(imageUrl: String) {
        secondaryDisplay?.updateDefaultImageUrl(imageUrl)
    }

    actual fun updateCartItems(
        cartItems: List<CartItem>,
        cartTotalQty: Double,
        cartSubTotal: Double,
        cartTotal: Double,
        cartTotalTax: Double,
        cartItemTotalDiscount: Double,
        cartNetDiscounts: Double,
        currencySymbol: String
    ){

        secondaryDisplay?.checkAndUpdateCartDetails(
            currentCarts=cartItems,
            cartTotalQty=cartTotalQty,
            cartTotal = cartTotal,
            cartSubTotal = cartSubTotal,
            cartTotalTax = cartTotalTax,
            cartNetDiscounts = cartNetDiscounts,
            cartItemTotalDiscount = cartItemTotalDiscount,
            currencySymbol = currencySymbol)
     }

}