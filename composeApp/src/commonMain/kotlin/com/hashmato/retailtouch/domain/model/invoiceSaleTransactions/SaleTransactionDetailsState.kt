package com.hashmato.retailtouch.domain.model.invoiceSaleTransactions

import com.hashmato.retailtouch.domain.model.dropdown.DeliveryType
import com.hashmato.retailtouch.domain.model.dropdown.MemberType
import com.hashmato.retailtouch.domain.model.dropdown.StatusType
import com.hashmato.retailtouch.domain.model.location.Location
import com.hashmato.retailtouch.domain.model.paymentType.PaymentMethod
import com.hashmato.retailtouch.domain.model.products.PosInvoice
import com.hashmato.retailtouch.domain.model.products.PosInvoiceDetail
import com.hashmato.retailtouch.domain.model.products.PosPayment

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
