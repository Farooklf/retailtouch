package com.hashmato.retailtouch.domain.model.home

import com.hashmato.retailtouch.domain.model.employee.POSEmployee
import com.hashmato.retailtouch.domain.model.login.AuthenticateDao


data class HomeUIState(
    var isError : Boolean = false,
    var errorMsg : String = "",
    val showExitConfirmationDialog: Boolean = false,
    val isLoading: Boolean = false,
    val isFromSplash: Boolean = false,
    val isBlur: Boolean = true,
    val isSyncEveryThing: Boolean = false,
    val hasEmployeeLoggedIn: Boolean = false,
    val loadingMessage: String = "Loading...",
    val isSyncClicked: Boolean = false,
    val homeItemList : List<HomeScreenItem> = listOf(),
    val rotationAngle: Float = 0f, // Store the current rotation angle

    val employeeRole: String = "",
    val POSEmployee: POSEmployee = POSEmployee(),
    val authUser: AuthenticateDao = AuthenticateDao(),

    )
