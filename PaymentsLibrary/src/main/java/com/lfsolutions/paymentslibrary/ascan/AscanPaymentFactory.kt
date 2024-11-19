package com.lfsolutions.paymentslibrary.ascan

import com.lfsolutions.paymentslibrary.Payment
import com.lfsolutions.paymentslibrary.PaymentFactory

class AscanPaymentFactory: PaymentFactory() {
    override fun createPayment(): Payment {
        return AscanPayment()
    }
}