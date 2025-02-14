package com.hashmato.retailtouch.utils.payment

import androidx.compose.runtime.Composable
import io.ktor.client.HttpClient

actual class PaymentProvider actual constructor() {

    // fun getResultFlow(): SharedFlow<String>
    @Composable
    actual fun launchExternalApp(
        amount: Double,
        paymentTypes: PaymentLibTypes,
        processorName: String
    ) {
    }

    actual fun launchApi(
        paymentTypes: PaymentLibTypes,
        httpClient: HttpClient
    ) {
    }

    actual fun processResult(result: String) {
    }
}