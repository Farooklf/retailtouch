package com.lfssolutions.retialtouch.utils.secondDisplay

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lfssolutions.retialtouch.R
import com.lfssolutions.retialtouch.domain.model.products.CartItem

class CartItemAdapter(private val items: List<CartItem>) : RecyclerView.Adapter<CartItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productNameTv: TextView = itemView.findViewById<TextView>(R.id.product_name_tv)
        val productPriceTv: TextView = itemView.findViewById<TextView>(R.id.product_price_tv)
        val subTotalTv: TextView = itemView.findViewById<TextView>(R.id.product_subtotal_tv)
        val parentLayout: LinearLayout = itemView.findViewById(R.id.parent_layout)
        var discountLayout: LinearLayout = itemView.findViewById(R.id.discount_layout)
        var discountPriceTv: TextView = itemView.findViewById(R.id.discount_price_tv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.payment_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if ((position % 2) == 0) {
            holder.parentLayout.setBackgroundResource(R.drawable.card10_grey_border)
        } else {
            holder.parentLayout.setBackgroundResource(android.R.color.transparent)
        }
        val cartItem = items[position]
        holder.productNameTv.text = "${cartItem.stock.name}\n[${cartItem.stock.inventoryCode}]"
        holder.productPriceTv.text = "${cartItem.qty}x${cartItem.price}"
        holder.subTotalTv.text = "${cartItem.qty*cartItem.price}${cartItem.currencySymbol} "
        if(cartItem.discount > 0 || cartItem.currentDiscount > 0){
            holder.discountLayout.visibility=View.VISIBLE
            holder.discountPriceTv.text="${cartItem.currencySymbol}${cartItem.calculateDiscount()}"
        }
    }

    override fun getItemCount() = items.size
}