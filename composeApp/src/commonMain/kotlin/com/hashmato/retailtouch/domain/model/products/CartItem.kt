package com.hashmato.retailtouch.domain.model.products

import com.hashmato.retailtouch.domain.model.members.MemberItem
import com.hashmato.retailtouch.domain.model.promotions.PromotionDetails
import com.hashmato.retailtouch.utils.DateTimeUtils.getCurrentDateAndTimeInEpochMilliSeconds
import com.hashmato.retailtouch.utils.DoubleExtension.roundTo
import kotlinx.serialization.Serializable

@Serializable
data class SaleOnHoldRecordDao(
    val id:Long=0,
    val item:CRSaleOnHold=CRSaleOnHold()
)

@Serializable
data class CRSaleOnHold(
    var collectionId:Long=0,
    var ts : Long = getCurrentDateAndTimeInEpochMilliSeconds(),
    var grandTotal: Double=0.0,
    var member: MemberItem?=null,
    val items: MutableList<CartItem> = mutableListOf(),
)

@Serializable
data class CartItem(
    val index: Int = 0,
    val stock: Stock = Stock(),
    val id: Long = stock.id,
    val name: String= stock.name,
    val categoryId: Int = stock.categoryId,
    val productId: Long = stock.productId,
    var price: Double = stock.price,
    var tax: Double = stock.tax,
    var qty: Double = 1.0,
    val inventoryCode: String = stock.inventoryCode,
    val barcode: String = stock.barcode,
    val imagePath: String = stock.imagePath,
    val fgColor: String = stock.fgColor,
    val bgColor: String = stock.bgColor,
    val sortOrder: Int = stock.sortOrder,
    //Promotions
    var promotion: PromotionDetails? = null,
    var promotionName: String? = null,
    var oldPrice: Double = 0.0,
    var amount: Double = 0.0,
    var exchange: Boolean = false,
    var salesTaxInclusive: Boolean = false,
    var promotionActive: Boolean = false,
    var promotionByQuantity: Boolean = false,
    var discountIsInPercent: Boolean = false,
    var isCombo: Boolean = false,
    var discount: Double = 0.0,
    var promoDiscount: Double = 0.0,

    var currencySymbol: String = "$",
){

    // Calculate the current price (with promotion if active)
     val currentPrice: Double
        get() = if (promotionActive) promotion?.promotionPrice ?: price else price

    // Calculate the total discount (item + promo)
    val currentDiscount: Double
        get() = if (promotionActive) {
            (price * qty) - getFinalPrice()
        } else {
            getItemDiscount()
        }

    fun calculateDiscount(): Double {
        if (promotionActive) {
            val y = getFinalPrice()
            val discountAmount = (price * qty) - y
            return discountAmount/*.roundTo(2)*/
        } else {
            return if (discountIsInPercent) {
                ((currentPrice * discount / 100) * qty)
            } else {
                discount
            }
        }
    }

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
                discount.roundTo(2) //(discount * qty).roundTo(2)
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
        var amt = if (promotionByQuantity) amount.times(qty) else currentPrice * qty
        //println("current Amt: $amt")

        if (amt > 0 ) {
            if (discount > 0) {
                if (discountIsInPercent) {
                    // Apply percentage discount directly, ensuring discount is valid
                    amt -= (amt * discount / 100.0).coerceAtMost(amt)
                } else {
                    // Apply flat discount, ensuring it doesn’t exceed the amount
                    val totalDiscount = (discount).coerceAtMost(amt)
                    amt -= totalDiscount
                }
            }
        }
        return amt.roundTo(2)
    }

    fun getFinalPriceWithoutDiscounts():Double{
        return if (promotionByQuantity) amount.times(qty) else currentPrice * qty
    }

    // Calculate the final price without tax
    fun getFinalPriceWithoutTax(): Double {
        var amt = if (promotionByQuantity) (amount * qty) else (currentPrice * qty)

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


