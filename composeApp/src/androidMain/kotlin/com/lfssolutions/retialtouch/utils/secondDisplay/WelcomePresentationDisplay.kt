package com.lfssolutions.retialtouch.utils.secondDisplay

import android.app.Presentation
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Display
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.lfssolutions.retialtouch.R
import com.lfssolutions.retialtouch.domain.model.products.CartItem

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

    fun checkAndUpdateCartDetails(
        currentCarts: List<CartItem>,
        cartTotal: Double,
        cartSubTotal: Double,
        cartTotalTax: Double,
        cartTotalDiscount: Double,
        currencySymbol: String,
    )
    {
        if (currentCarts.isNotEmpty()) {
            findViewById<ConstraintLayout>(R.id.cart_display_layout).visibility = View.VISIBLE
            findViewById<ConstraintLayout>(R.id.display_layout).visibility = View.GONE
            findViewById<ConstraintLayout>(R.id.change_layout).visibility = View.GONE
            findViewById<LinearLayout>(R.id.product_view).visibility = View.VISIBLE
            val imageSlider = findViewById<ImageSlider>(R.id.image_slider)
            val cartItemRv = findViewById<RecyclerView>(R.id.cartItemRv)
            val discountView = findViewById<LinearLayout>(R.id.discount_view)
            val discountDivider = findViewById<View>(R.id.discount_line)
            val totalTv = findViewById<TextView>(R.id.total_tv)
            val subTotalTv = findViewById<TextView>(R.id.subtotal_tv)
            val taxTv = findViewById<TextView>(R.id.total_tv)
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

            subTotalTv.text = "$currencySymbol $cartSubTotal"
            taxTv.text = "$currencySymbol $cartTotalTax"

            if(cartTotalDiscount>0.0){
                discountView.visibility = View.VISIBLE
                discountDivider.visibility = View.VISIBLE
                discountTv.text = "$currencySymbol  $cartTotalDiscount"

            }else{
                discountView.visibility = View.GONE
                discountDivider.visibility = View.GONE
            }

            val cartGrandTotal="$currencySymbol $cartTotal"
            totalTv.text = context_.getString(R.string.grand_total,cartGrandTotal)
            //findViewById<TextView>(R.id.balance_tv).text = "0"

            //findViewById<TextView>(R.id.paid_due_tv).text = "0"

            //getTotalPrice(array_,taxesResponse, findViewById(R.id.total_tv))

        }
        else {
            displayDefaultImage()
        }
    }
}