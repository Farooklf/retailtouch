package com.lfssolutions.retialtouch.utils.secondDisplay

import com.lfssolutions.retialtouch.ShareDisplayObject
import com.lfssolutions.retialtouch.domain.model.products.CartItem

actual class SecondaryDisplayServiceProvider actual constructor() {
    private val secondaryDisplay = ShareDisplayObject.secondaryDisplayInstance

    actual fun updateDefaultImage(imageUrl: String) {
        secondaryDisplay?.updateDefaultImageUrl(imageUrl)
    }

    actual fun updateCartItems(
        cartItems: List<CartItem>,
        cartSubTotal: Double,
        cartTotal: Double,
        cartTotalTax: Double,
        cartTotalDiscount: Double,
        currencySymbol: String
    ){
        secondaryDisplay?.checkAndUpdateCartDetails(cartItems,cartTotal,cartSubTotal,cartTotalTax,cartTotalDiscount,currencySymbol)
     }

}