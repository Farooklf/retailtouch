package com.lfssolutions.retialtouch.domain.model.inventory

import com.lfssolutions.retialtouch.domain.model.promotions.PromotionDetails
import com.lfssolutions.retialtouch.utils.DoubleExtension.roundTo


data class CRShoppingCartItem(
    val index: Int = 0,
    val id:Int=0,
    val stock: Stock=Stock(),
    var promotion: PromotionDetails? = null,
    var promotionName: String? = null,
    var price: Double = stock.stockPrice ?: 0.0,
    var oldPrice: Double = 0.0,
    var amount: Double? = 0.0,
    var qty: Double = 1.0,
    var exchange: Boolean = false,
    var salesTaxInclusive: Boolean = false,
    var promotionActive: Boolean = false,
    var promotionByQuantity: Boolean = false,
    var discountIsInPercent: Boolean = false,
    var discount: Double = 0.0,
    var promoDiscount: Double = 0.0,
    var tax: Double = stock.tax ?: 0.0,
    var code: String = stock.barcode ?: stock.inventoryCode ?:""
){

    // Calculate the current price (with promotion if active)
    private val currentPrice: Double
        get() = if (promotionActive) promotion?.promotionPrice ?: price else price

    // Calculate the total discount (item + promo)
    val currentDiscount: Double
        get() = if (promotionActive) {
            (price * qty) - getFinalPrice()
        } else {
            getItemDiscount()
        }

    fun calculateDiscount(): Double {
        if (discount < 0) return 0.0
        if (promotionActive) {
            val y = getFinalPrice()
            val discountAmount = (price * qty) - y
            return discountAmount.roundTo(2)
        } else {
            return if (discountIsInPercent) {
                ((currentPrice * discount / 100) * qty).roundTo(2)
            } else {
                (discount * qty).roundTo(2)
            }
        }
    }


    // Get the promo discount if promotion is active
    fun getPromotionDiscount(): Double {
        return if (promotionActive) {
            val discountAmount = (price * qty) - getFinalPrice()
            discountAmount.roundTo(2)
        } else {
            0.0
        }
    }

    // Calculate item discount including any promo discount
    fun getItemDiscount(): Double {
         if(discount < 0) return 0.0

        return if (discountIsInPercent) {
                ((currentPrice * discount / 100) * qty).roundTo(2)
            } else {
                (discount * qty).roundTo(2)
            }
         }


    // Calculate tax based on the price and tax settings
    fun calculateTax(): Double {
        if(tax<0) return 0.0
        val actualValue = if (promotionByQuantity) (amount!! * qty) else (qty * currentPrice)
        return if (salesTaxInclusive) {
            (actualValue * tax) / (tax + 100)
        } else {
            (actualValue * tax) / 100
        }
    }


    // Get final price with tax applied
    fun getFinalPriceWithTax(): Double {
        val amt = currentPrice * qty
        var dis = 0.0

        if (amt > 0) {
            if (discount > 0) {
                if (discountIsInPercent) {
                    if (discount < 100.0) {
                        dis -= ((amt * discount) / 100.0)
                    }
                } else if (discount <= amt) {
                    dis -= discount
                }
            }

            if (salesTaxInclusive) {
                val tempTax = calculateTax()
                return amt + (qty * tempTax)
            } else {
                return amt
            }
        } else {
            return 0.0
        }
    }


    // Calculate the final price with all conditions applied
     fun getFinalPrice(): Double {
        var amt = if (promotionByQuantity) amount?.times(qty) else currentPrice * qty

        if (amt != null) {
            if (amt > 0 || exchange) {
                if (discount > 0) {
                    if (discountIsInPercent) {
                        // Apply percentage discount directly, ensuring discount is valid
                        amt -= (amt * discount / 100.0).coerceAtMost(amt)
                    } else {
                        // Apply flat discount, ensuring it doesn’t exceed the amount
                        val totalDiscount = (discount * qty).coerceAtMost(amt)
                        amt -= totalDiscount
                    }
                }
            }
        }
        if (amt != null) {
            return amt.coerceAtLeast(0.0)
        }
        return 0.0
    }

    // Calculate the final price without tax
    fun getFinalPriceWithoutTax(): Double {
        var amt = if (promotionByQuantity) (amount!! * qty) else (currentPrice * qty)

        if (amt > 0) {
            if(discount>0){
                amt -= if(discountIsInPercent){
                    // Apply percentage discount directly, ensuring discount is valid
                    ((amt * discount) / 100.0).coerceAtMost(amt)
                }else{
                    discount.coerceAtMost(amt)
                }
            }
        }
        return amt.coerceAtLeast(0.0)
    }


}


