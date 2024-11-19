package com.lfsolutions.paymentslibrary

import android.content.Context
import android.content.pm.PackageManager
import com.lfsolutions.paymentslibrary.ascan.AscanPaymentFactory
import com.lfsolutions.paymentslibrary.nets.NetsPaymentFactory
import com.lfsolutions.paymentslibrary.paytm.PaytmPaymentFactory
import com.lfsolutions.paymentslibrary.rfm.RFMPaymentFactory

enum class Payments {
    ASCAN,
    NETS,
    PAYTM,
    RFM
}

const val RFM_REQUEST_CODE = 1001
const val ASCAN_REQUEST_CODE = 1002

fun getPaymentFactory(payments: Payments): PaymentFactory {
    // Determine which payment factory to use based on some criteria
    return when (payments) {
        Payments.ASCAN -> {
            AscanPaymentFactory()
        }
        Payments.NETS -> {
            NetsPaymentFactory()
        }
        Payments.PAYTM -> {
            PaytmPaymentFactory()
        }
        Payments.RFM -> {
            RFMPaymentFactory()
        }
    }
}

fun isPackageInstalled(packageName: String, context: Context): Boolean {
    val packageManager: PackageManager = context.packageManager
    return try {
        packageManager.getPackageInfo(packageName, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}