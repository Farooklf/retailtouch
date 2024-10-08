package com.lfssolutions.retialtouch.domain.model.paymentType

import com.lfssolutions.retialtouch.domain.model.dropdown.DeliveryType
import com.lfssolutions.retialtouch.domain.model.dropdown.StatusType
import com.lfssolutions.retialtouch.domain.model.productWithTax.ProductTaxItem


data class PaymentTypeUIState(
    var isLoading : Boolean = false,
    var currencySymbol : String = "$",
    val deliveryTypeList : List<DeliveryType> = listOf(),
    val statusTypeList : List<StatusType> = listOf(),
    val selectedDeliveryType : DeliveryType = DeliveryType(),
    val selectedStatusType : StatusType = StatusType(),
    val remark : String = "",
    val selectedDateTime : String ="",
    var memberId: Int = 0,
    var remainingAmount : Double = 0.0,
    var totalAmount: Double = 0.0,
    var minValue: Double = 1.0,
    var inputAmount  : String = "",
    var inputDiscountError  : String? = null,
    var remainingLabel  : String = "Remaining",
    var totalLabel  : String = "Amount To Pay",
    val paymentList : List<PaymentTypeItem> = listOf(),
    val selectedPayment: PaymentTypeItem = PaymentTypeItem(),
    var isShowCalculator : Boolean = false,
    val scannedPosList: List<ProductTaxItem> = listOf(),
)
