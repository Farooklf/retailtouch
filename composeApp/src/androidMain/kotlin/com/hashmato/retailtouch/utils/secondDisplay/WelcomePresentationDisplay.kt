package com.hashmato.retailtouch.utils.secondDisplay

import android.annotation.SuppressLint
import android.app.Presentation
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Display
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.hashmato.retailtouch.R
import com.hashmato.retailtouch.domain.model.products.CartItem

class WelcomePresentationDisplay(
    private val context_: Context,
    display: Display,
    private val urlString: String?
): Presentation(context_, display) {

    var displayModeId: Int = 0
    private var defaultImageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?){
        setContentView(R.layout.activity_url_screen_new)

        if (Build.VERSION.SDK_INT >= 26) {
            window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            window?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
        }
    }

    fun updateDefaultImageUrl(url: String) {
        defaultImageUrl = url
        displayDefaultImage()
    }

    private fun displayDefaultImage() {
        findViewById<ConstraintLayout>(R.id.display_layout).visibility = View.VISIBLE
        findViewById<LinearLayout>(R.id.product_view).visibility = View.GONE
        val imageView = findViewById<ImageView>(R.id.online_image_iv)
        val imageLayout = findViewById<ConstraintLayout>(R.id.default_image_layout)

        if (defaultImageUrl.isBlank()) {
            imageView.visibility = View.GONE
            imageLayout.visibility = View.VISIBLE
        } else {
            imageView.visibility = View.VISIBLE
            imageLayout.visibility = View.GONE
            imageView.load(defaultImageUrl)
        }
    }

    @SuppressLint("SetTextI18n")
    fun checkAndUpdateCartDetails(
        currentCarts: List<CartItem>,
        cartTotalQty: Double,
        cartTotal: Double,
        cartSubTotal: Double,
        cartTotalTax: Double,
        cartItemTotalDiscount: Double,
        cartNetDiscounts: Double,
        currencySymbol: String,
    )
    {
        println("cartTotalQty: $cartTotalQty | cartSubTotal: $cartSubTotal | GrandTotal : $cartTotal | TotalTax : $cartTotalTax | TotalDiscount : $cartNetDiscounts | ItemTotalDiscount: $cartItemTotalDiscount  | cartItems: $currentCarts ")
        if (currentCarts.isNotEmpty()) {
            findViewById<ConstraintLayout>(R.id.cart_display_layout).visibility = View.VISIBLE
            findViewById<ConstraintLayout>(R.id.display_layout).visibility = View.GONE
            findViewById<ConstraintLayout>(R.id.change_layout).visibility = View.GONE
            findViewById<LinearLayout>(R.id.product_view).visibility = View.VISIBLE
            val imageSlider = findViewById<ImageSlider>(R.id.image_slider)
            val cartItemRv = findViewById<RecyclerView>(R.id.cartItemRv)
            val discountView = findViewById<LinearLayout>(R.id.discount_view)
            val itemDiscountView = findViewById<LinearLayout>(R.id.item_discount_view)
            val discountDivider = findViewById<View>(R.id.discount_line)
            val itemDiscountDivider = findViewById<View>(R.id.item_discount_divider)
            val totalTv = findViewById<TextView>(R.id.total_tv)
            val qtyTv = findViewById<TextView>(R.id.qty_tv)
            val subTotalTv = findViewById<TextView>(R.id.subtotal_tv)
            val itemDiscountTv = findViewById<TextView>(R.id.item_discount_tv)
            val taxTv = findViewById<TextView>(R.id.tax_tv)
            val discountTv = findViewById<TextView>(R.id.discount_tv)

            val array = currentCarts.reversed()
            val imageList = ArrayList<SlideModel>()
            array.forEach { cartItem ->
                if (cartItem.stock.imagePath.isBlank()) {
                    imageList.add(
                        SlideModel(
                            R.drawable.app_logo,
                            cartItem.stock.name.uppercase(),
                            ScaleTypes.CENTER_CROP
                        )
                    )
                } else {
                    imageList.add(
                        SlideModel(
                            cartItem.stock.imagePath,
                            cartItem.stock.name.uppercase(),
                            ScaleTypes.CENTER_CROP
                        )
                    )
                }
            }
            imageSlider.setImageList(imageList.toList(), ScaleTypes.CENTER_CROP)

            //Bind Cart List
            cartItemRv.layoutManager= LinearLayoutManager(context_)
            cartItemRv.adapter=CartItemAdapter(currentCarts)
            cartItemRv.isNestedScrollingEnabled=false

            qtyTv.text = "$cartTotalQty"
            subTotalTv.text = "$currencySymbol $cartSubTotal"
            taxTv.text = "$currencySymbol $cartTotalTax"

            if(cartItemTotalDiscount>0.0){
                itemDiscountView.visibility = View.VISIBLE
                itemDiscountDivider.visibility = View.VISIBLE
                itemDiscountTv.text = "$currencySymbol  $cartItemTotalDiscount"

            }else{
                itemDiscountView.visibility = View.GONE
                itemDiscountDivider.visibility = View.GONE
            }

            if(cartNetDiscounts>0.0){
                discountView.visibility = View.VISIBLE
                discountDivider.visibility = View.VISIBLE
                discountTv.text = "$currencySymbol  $cartNetDiscounts"

            }else{
                discountView.visibility = View.GONE
                discountDivider.visibility = View.GONE
            }

            val cartGrandTotal="$currencySymbol $cartTotal"
            totalTv.text = context_.getString(R.string.grand_total,cartGrandTotal)
        }
        else {
            displayDefaultImage()
        }
    }
}