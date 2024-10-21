package com.lfssolutions.retialtouch.domain.model.paymentType

data class CRSplitPaymentItem(
    val paymentMethod: PaymentMethod = PaymentMethod(),
    var amount: Double = 0.0,
    var actualAmount: Double = 0.0,
    var change: Double = 0.0
) {
    companion object {
        fun empty(): CRSplitPaymentItem = CRSplitPaymentItem()
    }
}
