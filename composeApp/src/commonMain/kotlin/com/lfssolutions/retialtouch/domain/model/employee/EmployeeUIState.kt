package com.lfssolutions.retialtouch.domain.model.employee

import org.jetbrains.compose.resources.StringResource

data class EmployeeUIState(
    val isLoading: Boolean = false,

    val employeeCode: String = "",
    val pin: String = "",

    var employeeCodeError: StringResource? = null,
    var pinError: StringResource? = null,

    var isEmployeeLoginSuccess: Boolean = false,
    val isLogoutFromServer: Boolean = false,
)
