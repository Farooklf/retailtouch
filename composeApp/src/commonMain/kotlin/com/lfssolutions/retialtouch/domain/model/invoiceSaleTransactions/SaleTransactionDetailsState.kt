package com.lfssolutions.retialtouch.domain.model.invoiceSaleTransactions

import com.lfssolutions.retialtouch.domain.model.dropdown.DeliveryType
import com.lfssolutions.retialtouch.domain.model.dropdown.MemberType
import com.lfssolutions.retialtouch.domain.model.dropdown.StatusType
import com.lfssolutions.retialtouch.domain.model.location.Location
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentMethod
import com.lfssolutions.retialtouch.domain.model.products.PosInvoice
import com.lfssolutions.retialtouch.domain.model.products.PosInvoiceDetail
import com.lfssolutions.retialtouch.domain.model.products.PosPayment

data class SaleTransactionDetailsState(
    var isLoading: Boolean = false,
    val isError:Boolean=false,
    val errorMessage:String="",
    var currencySymbol : String = "$",
    val saleRecord:SaleRecord=SaleRecord(),
    //location
    val location : Location? = Location(),
    val typeList: List<DeliveryType> = mutableListOf(),
    val statusList: List<StatusType> = mutableListOf(),
    val member: MemberType = MemberType(),
    val deliveryType: DeliveryType? = DeliveryType(),
    val statusType: StatusType = StatusType(),
    val isFilterApplied:Boolean=false,
    val type:Int=0,
    val status:Int=0,
    val memberId:Int=0,

    var showPaymentModeDialog: Boolean = false,
    val selectedPaymentMethod:PaymentMethod=PaymentMethod(),
    val clickedPayment: PosPayment =PosPayment(),

    val paymentModes: List<PaymentMethod> = mutableListOf(),
    val posInvoice:PosInvoice? = PosInvoice(),
    val posInvoiceDetail: List<PosInvoiceDetail>? = mutableListOf(),
)
