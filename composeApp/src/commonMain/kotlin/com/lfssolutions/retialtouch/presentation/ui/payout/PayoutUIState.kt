package com.lfssolutions.retialtouch.presentation.ui.payout

import org.jetbrains.compose.resources.StringResource

data class PayoutUIState(
    val isSyncLoader:Boolean=false,
    val isError:Boolean=false,
    val errorMsg:String="",
    val memberName:String="",
    val expenseDescription:String="",
    val expensePayTo:String="",
    val expenseAmount:String="",

    val errorDescription:StringResource?=null,
    val errorPayTo:StringResource?=null,
    val errorAmount:StringResource?=null,

)
