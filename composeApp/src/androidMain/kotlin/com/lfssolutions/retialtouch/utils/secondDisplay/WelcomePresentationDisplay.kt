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
        findViewById<LinearLayout>(R.id.display_layout).visibility = View.VISIBLE
        findViewById<LinearLayout>(R.id.product_view).visibility = View.GONE
        val imageView = findViewById<ImageView>(R.id.online_image_iv)
        val relativeView = findViewById<RelativeLayout>(R.id.default_image_layout)

        if (defaultImageUrl.isBlank()) {
            imageView.visibility = View.GONE
            relativeView.visibility = View.VISIBLE
        } else {
            imageView.visibility = View.VISIBLE
            relativeView.visibility = View.GONE
            imageView.load(defaultImageUrl)
        }
    }

    fun checkAndUpdateCartDetails(
        currentCarts: Array<CartItem>,
        cartTotal: Double,
        cartSubTotal: Double,
        cartTotalTax: Double,
        cartTotalDiscount: Double,
        currencySymbol: String,
    )
    {
        if (currentCarts.isNotEmpty()) {
            findViewById<LinearLayout>(R.id.cart_display_layout).visibility = View.VISIBLE
            findViewById<LinearLayout>(R.id.display_layout).visibility = View.GONE
            findViewById<RelativeLayout>(R.id.change_layout).visibility = View.GONE
            findViewById<LinearLayout>(R.id.product_view).visibility = View.VISIBLE
            val imageSlider = findViewById<ImageSlider>(R.id.image_slider)
            val array = currentCarts.reversedArray()
            val imageList = ArrayList<SlideModel>()
            array.forEach { cartItem ->

                if (cartItem.stock.imagePath.isBlank()) {
                    imageList.add(
                        SlideModel(
                            R.drawable.app_logo,
                            cartItem.stock.name,
                            ScaleTypes.CENTER_CROP
                        )
                    )
                } else {
                    imageList.add(
                        SlideModel(
                            cartItem.stock.imagePath,
                            cartItem.stock.name,
                            ScaleTypes.CENTER_CROP
                        )
                    )
                }
            }
            imageSlider.setImageList(imageList.toList(), ScaleTypes.CENTER_CROP)

//            var rv = findViewById<RecyclerView>(R.id.order_holder_rv)
//            val layout = LinearLayoutManager(context_)
//            layout.reverseLayout = false
//            rv.layoutManager = layout
//            val dividerItemDecoration = DividerItemDecoration(
//                context_,
//                layout.orientation
//            )
//            findViewById<RecyclerView>(R.id.order_holder_rv).addItemDecoration(
//                dividerItemDecoration
//            )

            val order_ll = findViewById<LinearLayout>(R.id.order_holder_layout)
            order_ll.removeAllViews()
            val array_ = ArrayList<CartItem>()
            array_.addAll(array)
            PaymentCartListView(context_, array_).getViews().forEach { view ->
                order_ll.addView(view)
            }

            findViewById<TextView>(R.id.total_tv).text = "$currencySymbol $cartTotal"

            findViewById<TextView>(R.id.subtotal_tv).text = "$currencySymbol $cartSubTotal"


            cartTotalDiscount.let {
                findViewById<TextView>(R.id.discount_tv).text = "$currencySymbol $cartTotalDiscount"
            } ?: run {
                findViewById<TextView>(R.id.discount_tv).visibility = View.GONE
            }


            cartTotalTax.let {
                findViewById<TextView>(R.id.tax_tv).text = "$currencySymbol  $cartTotalTax"
            } ?: run {
                findViewById<TextView>(R.id.tax_tv).visibility = View.GONE
            }

            findViewById<TextView>(R.id.balance_tv).text = "0"

            findViewById<TextView>(R.id.paid_due_tv).text = "0"

            //getTotalPrice(array_,taxesResponse, findViewById(R.id.total_tv))

        }
        else {
            displayDefaultImage()
        }
    }
}