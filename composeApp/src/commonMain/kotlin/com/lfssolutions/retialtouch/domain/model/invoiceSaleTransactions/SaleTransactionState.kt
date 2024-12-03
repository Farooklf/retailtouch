package com.lfssolutions.retialtouch.domain.model.invoiceSaleTransactions

import com.lfssolutions.retialtouch.domain.model.dropdown.DeliveryType
import com.lfssolutions.retialtouch.domain.model.dropdown.MemberType
import com.lfssolutions.retialtouch.domain.model.dropdown.StatusType
import com.lfssolutions.retialtouch.domain.model.login.LoginResponse
import com.lfssolutions.retialtouch.utils.DateTime.getCurrentDateTimeInPreferredFormat


data class SaleTransactionState(
    var isLoading: Boolean = false,
    val loginUser: LoginResponse = LoginResponse(),
    var currencySymbol : String = "$",

    val saleTransaction: List<SaleRecord> = mutableListOf(),

    //filter
    val memberList: List<MemberType> = mutableListOf(),
    val typeList: List<DeliveryType> = mutableListOf(),
    val statusList: List<StatusType> = mutableListOf(),
    val member: MemberType = MemberType(),
    val type: DeliveryType = DeliveryType(),
    val status: StatusType = StatusType(),
    val isDatePickerDialog : Boolean = false,
    val isFromDate : Boolean = false,
    var startDate : String = getCurrentDateTimeInPreferredFormat(),
    var endDate : String = getCurrentDateTimeInPreferredFormat(),
)