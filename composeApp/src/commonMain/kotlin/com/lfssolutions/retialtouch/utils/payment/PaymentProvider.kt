package com.lfssolutions.retialtouch.utils.payment

import androidx.compose.runtime.Composable
import io.ktor.client.HttpClient

expect class PaymentProvider() {

    @Composable
    fun launchExternalApp(amount:Double, paymentTypes: PaymentLibTypes,processorName:String)
    fun launchApi(paymentTypes: PaymentLibTypes,httpClient: HttpClient)
    fun processResult(result: String)
    // fun getResultFlow(): SharedFlow<String>
}

enum class PaymentLibTypes{
    ASCAN,
    NETS,
    PAYTM,
    RFM
}