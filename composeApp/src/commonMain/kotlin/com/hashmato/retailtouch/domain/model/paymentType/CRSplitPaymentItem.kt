package com.hashmato.retailtouch.domain.model.paymentType

data class CRSplitPaymentItem(
    val paymentMethod: PaymentMethod = PaymentMethod(),
    var paymentTypeId: Int = 0,
    var amount: Double = 0.0,
    var actualAmount: Double = 0.0,
    var change: Double = 0.0
) {
    companion object {
        fun empty(): CRSplitPaymentItem = CRSplitPaymentItem()
    }
}
