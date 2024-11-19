package com.lfsolutions.paymentslibrary

abstract class PaymentFactory {
    abstract fun createPayment(): Payment

}