package com.lfsolutions.paymentslibrary.paytm

import android.content.Context
import android.content.Intent
import com.lfsolutions.paymentslibrary.Payment

class PaytmPayment:Payment {
    override fun preProcess(context: Context, processorName: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun process(context: Context, amount: Double) {
        TODO("Not yet implemented")
    }

    override fun postProcess(requestCode: Int, resultCode: Int, data: Intent?, context: Context): Double {
        TODO("Not yet implemented")
    }
}