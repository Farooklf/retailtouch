package com.lfsolutions.paymentslibrary.paytm.paymentprocessors

data class Head(
    val channelId: String,
    var checksum: String,
    var requestTimeStamp: String = "",
    val version: String
)

data class PaytmRequest(
    val head: Head,
    val body: Body
) {
    fun getPaymentStatusCheckRequest(checksum: String): PaymentStatusCheckRequest {
        val bodyStatusRequest = PaytmStatusCheckBody(
            body.paytmMid,
            body.paytmTid,
            head.requestTimeStamp,
            body.merchantTransactionId
        )
        head.checksum = checksum
        val headStatusRequest =
            head
        return PaymentStatusCheckRequest(bodyStatusRequest, headStatusRequest)
    }
}


data class PaytmStatusCheckBody(
    val paytmMid: String,
    val paytmTid: String,
    val transactionDateTime: String,
    val merchantTransactionId: String,
)

data class Body(
    val paytmMid: String,
    val paytmTid: String,
    val transactionDateTime: String,
    val merchantTransactionId: String,
    val transactionAmount: String
)

data class PaymentStatusCheckRequest(
    val body: PaytmStatusCheckBody,
    val head: Head
)