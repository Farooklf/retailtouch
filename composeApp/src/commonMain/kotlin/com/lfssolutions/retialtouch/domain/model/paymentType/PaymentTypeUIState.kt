package com.lfssolutions.retialtouch.domain.model.paymentType

import com.lfssolutions.retialtouch.domain.model.dropdown.DeliveryType
import com.lfssolutions.retialtouch.domain.model.dropdown.StatusType
import com.lfssolutions.retialtouch.domain.model.productWithTax.PosInvoiceDetail
import com.lfssolutions.retialtouch.domain.model.productWithTax.PosPayment
import com.lfssolutions.retialtouch.domain.model.productWithTax.PosUIState
import com.lfssolutions.retialtouch.domain.model.productWithTax.ProductTaxItem


data class PaymentTypeUIState(
    var isLoading : Boolean = false,
    var errorMsg : String = "",
    var isPaymentClose : Boolean = false,

    var currencySymbol : String = "$",
    val deliveryTypeList : List<DeliveryType> = listOf(),
    val statusTypeList : List<StatusType> = listOf(),
    val selectedDeliveryType : DeliveryType = DeliveryType(),
    val selectedStatusType : StatusType = StatusType(),
    val remark : String = "",
    val selectedDateTime : String ="",
    var memberId: Int = 0,

    var minValue: Double = 1.0,
    var inputAmount  : String = "",
    var inputDiscountError  : String? = null,
    var remainingLabel  : String = "Remaining",
    var totalLabel  : String = "Amount To Pay",
    val paymentList : List<PaymentMethod> = listOf(),
    val selectedPayment: PaymentMethod = PaymentMethod(),

    val scannedPosList: List<ProductTaxItem> = listOf(),

    val grandTotal: Double = 0.0,
    val paymentTotal: Double = 0.0,
    var remainingBalance : Double = 0.0,
    val isPaid: Boolean = false,
    val selectedPaymentToDelete: Int = 0,
    val showDeletePaymentModeDialog: Boolean = false,
    val showPaymentCollectorDialog: Boolean = false,
    val roundToDecimal: Int = 2,

    val posUIState: PosUIState = PosUIState(),
    val posPayments: List<PosPayment> = emptyList(),
    val createdPayments: MutableList<PosPayment> = mutableListOf(),
    val posInvoiceDetails: MutableList<PosInvoiceDetail> = mutableListOf()
)
