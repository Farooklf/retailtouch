package com.lfssolutions.retialtouch.domain.model.paymentType

import com.lfssolutions.retialtouch.domain.model.dropdown.DeliveryType
import com.lfssolutions.retialtouch.domain.model.dropdown.StatusType
import com.lfssolutions.retialtouch.domain.model.products.PosPayment

data class PaymentTypeUIState(
    var isLoading: Boolean = false,
    var isPaymentScreen: Boolean = false,
    var errorMsg : String = "",
    var inputDiscountError : String? = "",
    var isPaymentClose : Boolean = false,
    val deliveryTypeList : List<DeliveryType> = listOf(),
    val statusTypeList : List<StatusType> = listOf(),
    val selectedDeliveryType : DeliveryType = DeliveryType(),
    val selectedStatusType : StatusType = StatusType(),
    val remark : String = "",
    val selectedDateTime : String ="",
    var minValue: Double = 1.0,
    var inputAmount  : String = "",
    var remainingLabel  : String = "Remaining",
    var totalLabel  : String = "Amount To Pay",
    val paymentList : List<PaymentMethod> = listOf(),
    val selectedPayment: PaymentMethod = PaymentMethod(),
    val paymentTotal: Double = 0.0,
    var grandTotal : Double = 0.0,
    var remainingBalance : Double = 0.0,
    val isPaid: Boolean = false,
    val selectedPaymentToDelete: Int = 0,
    val showDeletePaymentModeDialog: Boolean = false,
    val showPaymentCollectorDialog: Boolean = false,
    val roundToDecimal: Int = 2,
    val createdPayments: MutableList<PosPayment> = mutableListOf(),
)
