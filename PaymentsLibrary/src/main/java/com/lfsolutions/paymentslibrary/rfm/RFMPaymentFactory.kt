package com.lfsolutions.paymentslibrary.rfm

import com.lfsolutions.paymentslibrary.Payment
import com.lfsolutions.paymentslibrary.PaymentFactory

class RFMPaymentFactory: PaymentFactory() {
    override fun createPayment(): Payment {
       return RFMPayment()
    }
}