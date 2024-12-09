package com.lfssolutions.retialtouch.domain.model.invoiceSaleTransactions

import com.lfssolutions.retialtouch.domain.model.dropdown.DeliveryType
import com.lfssolutions.retialtouch.domain.model.dropdown.MemberType
import com.lfssolutions.retialtouch.domain.model.dropdown.StatusType
import com.lfssolutions.retialtouch.domain.model.login.LoginResponse
import com.lfssolutions.retialtouch.domain.model.posInvoices.PendingSale
import com.lfssolutions.retialtouch.domain.model.products.PosInvoice
import com.lfssolutions.retialtouch.utils.DateTime.getCurrentDate



data class SaleTransactionState(
    var isLoading: Boolean = false,
    val loginUser: LoginResponse = LoginResponse(),
    var currencySymbol : String = "$",
    val isError:Boolean=false,
    val errorMessage:String="",

    val transactionSales: List<SaleRecord> = mutableListOf(),
    val pendingSales: List<PendingSale> = mutableListOf(),

    var isSaleTransactionSync: Boolean = false,
    var isSalePendingSync: Boolean = false,
    var showPendingSalePopup: Boolean = false,

    val posInvoice: PosInvoice=PosInvoice(),

    //filter
    var isTypeFilter: Boolean = false,
    var isStatusFilter: Boolean = false,
    var isMemberFilter: Boolean = false,
    var isFromDateFilter: Boolean = false,
    var isEndDateFilter: Boolean = false,
    val memberList: List<MemberType> = mutableListOf(),
    val typeList: List<DeliveryType> = mutableListOf(),
    val statusList: List<StatusType> = mutableListOf(),
    val member: MemberType = MemberType(),
    val deliveryType: DeliveryType? = DeliveryType(),
    val statusType: StatusType = StatusType(),
    val type:Int=0,
    val status:Int=0,
    val memberId:Int=0,
    val isDatePickerDialog : Boolean = false,
    val isFromDate : Boolean = false,
    var startDate : String = getCurrentDate(),
    var endDate : String = getCurrentDate(),
)