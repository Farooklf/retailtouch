package com.lfssolutions.retialtouch.utils.payment

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.lfsolutions.paymentslibrary.PaymentFactory
import com.lfsolutions.paymentslibrary.Payments
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

actual class PaymentProvider actual constructor() {
    private val _resultFlow = MutableSharedFlow<String>()
    val resultFlow: SharedFlow<String> = _resultFlow

    @Composable
    actual fun launchExternalApp(amount: Double, paymentTypes: PaymentLibTypes, processorName: String) {
        val context = LocalContext.current
        val paymentFactory: PaymentFactory = getPaymentFactory(paymentTypes)
        val payment = paymentFactory.createPayment()
        val isPreProcessSuccess = payment.preProcess(context, processorName)
        if (isPreProcessSuccess) {
            Toast.makeText(context, "Amount $amount", Toast.LENGTH_SHORT).show()
            payment.process(context, amount)
        } else {
            //payment.process(context, amount)
            Toast.makeText(context, "Amount $amount", Toast.LENGTH_SHORT).show()
            Toast.makeText(context, "Please install the APP first!", Toast.LENGTH_LONG).show()
        }
    }

    actual fun processResult(result: String) {
        CoroutineScope(Dispatchers.Main).launch {
            _resultFlow.emit(result)
        }
    }

    // actual fun getResultFlow(): SharedFlow<String> = _resultFlow
    actual fun launchApi(paymentTypes: PaymentLibTypes, httpClient: HttpClient) {


    }

    private fun getPaymentFactory(paymentTypes: PaymentLibTypes) =
        when (paymentTypes) {
            PaymentLibTypes.ASCAN -> com.lfsolutions.paymentslibrary.getPaymentFactory(Payments.ASCAN)
            PaymentLibTypes.NETS -> com.lfsolutions.paymentslibrary.getPaymentFactory(Payments.NETS)
            PaymentLibTypes.PAYTM -> com.lfsolutions.paymentslibrary.getPaymentFactory(Payments.PAYTM)
            PaymentLibTypes.RFM -> com.lfsolutions.paymentslibrary.getPaymentFactory(Payments.RFM)
        }

}