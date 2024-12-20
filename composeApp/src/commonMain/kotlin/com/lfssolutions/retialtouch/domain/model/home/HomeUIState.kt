package com.lfssolutions.retialtouch.domain.model.home

import com.lfssolutions.retialtouch.domain.model.employee.EmployeeDao
import com.lfssolutions.retialtouch.domain.model.login.AuthenticateDao


data class HomeUIState(
    val isLoading: Boolean = false,
    val isFromSplash: Boolean = false,
    val isBlur: Boolean = true,
    val hasEmployeeLoggedIn: Boolean = false,
    val loadingMessage: String = "Loading...",
    val isSync: Boolean = false,
    val homeItemList : List<HomeScreenItem> = listOf(),
    val rotationAngle: Float = 0f, // Store the current rotation angle

    val employeeRole: String = "",
    val employeeDao: EmployeeDao = EmployeeDao(),
    val authUser: AuthenticateDao = AuthenticateDao(),

)
