package com.hashmato.retailtouch.utils.secondDisplay

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.hashmato.retailtouch.R
import com.hashmato.retailtouch.domain.model.products.CartItem
import java.util.ArrayList

class PaymentCartListView(
    private val context: Context,
    private val cartItemsArray: ArrayList<CartItem>,
) {

    @SuppressLint("SetTextI18n")
    fun getViews(): ArrayList<View> {
        val views = ArrayList<View>()
        cartItemsArray.forEachIndexed { position, cartItem ->
            val itemView: View = LayoutInflater.from(context).inflate(R.layout.payment_item, null)

            val param = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1.0f
            )
            itemView.layoutParams = param

            val productNameTv: TextView = itemView.findViewById<TextView>(R.id.product_name_tv)
            val productPriceTv: TextView = itemView.findViewById<TextView>(R.id.product_price_tv)
            val subTotalTv: TextView = itemView.findViewById<TextView>(R.id.product_subtotal_tv)
            // var lineItemHolder: LinearLayout = itemView.findViewById<LinearLayout>(R.id.combo_item_hold_ll)
            val parentLinearLayout: LinearLayout = itemView.findViewById(R.id.parent_layout)
            var discountLinearLayout: LinearLayout = itemView.findViewById(R.id.discount_layout)
            var discountLabelTv: TextView = itemView.findViewById(R.id.discount_label_tv)
            var discountPrePriceTv: TextView = itemView.findViewById(R.id.discount_price_tv)

           // lineItemHolder.removeAllViews()
            if ((position % 2) == 0) {
                parentLinearLayout.setBackgroundResource(R.drawable.card10_grey_border)
            } else {
                parentLinearLayout.setBackgroundResource(android.R.color.transparent)
            }
            val cartItem = cartItemsArray[position]
            productNameTv.text = "${cartItem.stock.name}"
            if(cartItem.discount > 0 || cartItem.currentDiscount > 0){
                discountLinearLayout.visibility=View.VISIBLE
                discountPrePriceTv.text="${cartItem.currencySymbol}${cartItem.calculateDiscount()}"
            }
            if (cartItem.isCombo) {

                //lineItemHolder.visibility = View.VISIBLE
                var lineItems = ""
//                 cartItem.selectedComboItems.forEach { comboGroup ->
//                     comboGroup.comb.forEach { comboItem ->
//                         var view =
//                             LayoutInflater.from(context).inflate(R.layout.lineitemitemone, null)
//                         view.findViewById<TextView>(R.id.line_items_tv).text =
//                             "${comboItem.name?.trimIndent()}"
//
//                         view.findViewById<TextView>(R.id.line_items_tv).setTextColor(context.resources.getColor(R.color.text_line_item))
//                         view.findViewById<TextView>(R.id.line_items_count_tv).setTextColor(context.resources.getColor(R.color.text_line_item))
//                         view.findViewById<TextView>(R.id.line_items_count_tv).text =
//                             "${comboItem.price} x ${comboItem.quantity}"
//
//                         var orderTagHolder = view.findViewById<LinearLayout>(R.id.ordertag_holder_ll)
//
//                         comboItem.orderTags?.forEach { tagItem ->
//                             tagItem.tags.filter { tag -> tag.isSelected }.let { orderTags ->
//                                 orderTags?.forEach { orderTag ->
//                                     var viewOrderTag =
//                                         LayoutInflater.from(context)
//                                             .inflate(R.layout.lineitemitemtwo, null)
//                                     viewOrderTag.findViewById<TextView>(R.id.line_items_tv).text =
//                                         "• ${orderTag.name?.trimIndent()}"
//                                     viewOrderTag.findViewById<TextView>(R.id.line_items_count_tv).text =
//                                         "${orderTag.price} x ${orderTag.quantity}"
//                                     orderTagHolder.addView(viewOrderTag)
//
//                                 }
//                             }
//                         }
//
//                         lineItemHolder.addView(view)
//
//                     }
//                 }

//                 var totalPrice = 0.0
//                 cartItem.generateMenuItemWithPortion().menuPortions?.forEach { menuPortion_ ->
//                     totalPrice += menuPortion_.price * cartItem.quantity
//                 }
//                 cartItem?.comboGroupList?.forEach { comboGroup ->
//                     comboGroup?.comboItems
//                         ?.forEach { comboItem ->
//                             totalPrice +=(comboItem.price * comboItem.quantity) * cartItem.quantity
//                             comboItem?.orderTags?.forEach { tagItem ->
//                                 tagItem.tags.filter { tag -> tag.isSelected }
//                                     ?.forEach { orderTag ->
//                                         totalPrice += (orderTag.price * orderTag.quantity) * cartItem.quantity
//
//                                     }
//                             }
//                         }
//                 }

//                 if (cartItem.generateMenuItemWithPortion().orderTags != null) {
//
//
//                     cartItem.generateMenuItemWithPortion().orderTags?.forEach { tagItem ->
//                         if (tagItem.tags.isNotEmpty()) {
//                             lineItemHolder.visibility = View.VISIBLE
//                         }
//                         tagItem.tags.filter { tag -> tag.isSelected }
//                             ?.forEach { orderTag ->
//                                 totalPrice += (orderTag.price * orderTag.quantity) * cartItem.quantity
//                                 var view =
//                                     LayoutInflater.from(context).inflate(R.layout.lineitemitemtwo, null)
//                                 view.findViewById<TextView>(R.id.line_items_tv).text =
//                                     "• ${orderTag.name?.trimIndent()}"
//                                 view.findViewById<TextView>(R.id.line_items_count_tv).text =
//                                     "${orderTag.price} x ${orderTag.quantity}"
//                                 lineItemHolder.addView(view)
//                             }
//                     }
//
//
//                 }



//                 if (cartItem.promotionsResponseString != null) {
//                     discountLinearLayout.visibility = View.VISIBLE
//                     discountPrePriceTv.text = String.format(
//                         Locale.US,
//                         "%.${Constants.decimalPrice}f",totalPrice)
//                     discountLabelTv.text =
//                         cartItem.generatePromotion()?.promotionDetails?.name
//                     productPriceTv.text = String.format(Locale.US,
//                         "%.${Constants.decimalPrice}f", ((totalPrice - getValueForPercentage(
//                             totalPrice,
//                             cartItem.generatePromotion()!!.result.promotionValue,
//                             cartItem.generatePromotion()!!.result.promotionValueType
//                         )))
//                     )
//                 } else {
//                     productPriceTv.text =
//                         String.format(Locale.US,"%.${Constants.decimalPrice}f", totalPrice.convert())
//                     discountLinearLayout.visibility = View.GONE
//                 }


            }
            else {

                //lineItemHolder.visibility = View.VISIBLE


//            var view = LayoutInflater.from(context).inflate(R.layout.lineitemitemone, null)
//            view.findViewById<TextView>(R.id.line_items_tv).text =
//                "• " + cartItem.menuPortion?.portionName
//            view.findViewById<TextView>(R.id.line_items_count_tv).visibility = View.GONE
//            lineItemHolder.addView(view)
                productNameTv.text =  "${productNameTv.text}[${cartItem.stock.inventoryCode}]"


//                 cartItem.menuPortion?.let {
//                     var totalPrice = 0.0
//
//                     cartItem.generateMenuItemWithPortion().menuPortions?.filter { menuPortion -> menuPortion.isSelected }
//                         ?.forEach { menuPortion_ ->
//                             totalPrice += menuPortion_.price * cartItem.quantity
//                         }
//                     cartItem.generateMenuItemWithPortion().orderTags?.forEach { tagItem ->
//                         tagItem.tags.filter { tag -> tag.isSelected }
//                             ?.forEach { orderTag ->
//                                 totalPrice += (orderTag.price * orderTag.quantity) * cartItem.quantity
//                             }
//                     }
                productPriceTv.text = "${cartItem.qty}x${cartItem.price}"
                subTotalTv.text = "${cartItem.currencySymbol} ${cartItem.qty*cartItem.price}"

//                     if (cartItem.promotionsResponseString != null) {
//                         discountLinearLayout.visibility = View.VISIBLE
//                         discountPrePriceTv.text = String.format(Locale.US,
//                             "%.${Constants.decimalPrice}f",totalPrice)
//                         discountLabelTv.text =
//                             cartItem.generatePromotion()?.promotionDetails?.name
//                         productPriceTv.text = String.format(Locale.US,
//                             "%.${Constants.decimalPrice}f", ((totalPrice - getValueForPercentage(
//                                 totalPrice,
//                                 cartItem.generatePromotion()!!.result.promotionValue,
//                                 cartItem.generatePromotion()!!.result.promotionValueType
//                             )))
//                         )
//                     } else {
//                         productPriceTv.text =
//                             String.format(Locale.US,
//                                 "%.${Constants.decimalPrice}f",
//                                 (totalPrice)
//                             )
//                         discountLinearLayout.visibility = View.GONE
//                     }



//                     if (cartItem.generateMenuItemWithPortion().orderTags != null) {
//
//
//                         cartItem.generateMenuItemWithPortion().orderTags?.forEach { tagItem ->
//                             if (tagItem.tags.isNotEmpty()) {
//                                 lineItemHolder.visibility = View.VISIBLE
//                             }
//                             tagItem.tags.filter { tag -> tag.isSelected }
//                                 ?.forEach { orderTag ->
//                                     // totalPrice += orderTag.price * orderTag.quantity
//                                     var view =
//                                         LayoutInflater.from(context)
//                                             .inflate(R.layout.lineitemitemtwo, null)
//                                     view.findViewById<TextView>(R.id.line_items_tv).text =
//                                         "• ${orderTag.name?.trimIndent()}"
//                                     view.findViewById<TextView>(R.id.line_items_count_tv).text =
//                                         "${orderTag.price} x ${orderTag.quantity}"
//                                     lineItemHolder.addView(view)
//                                 }
//                         }
//
//
//                     }
            }
            views.add(itemView)

        }

        return views
    }
}