package com.lfssolutions.retialtouch.domain.model.employee

data class EmployeeUIState(
    val isLoading: Boolean = false,

    val employeeCode: String = "",
    val pin: String = "",

    var employeeCodeError: String? = null,
    var pinError: String? = null,

    var isEmployeeLoginSuccess: Boolean = false,
    val isLogoutFromServer: Boolean = false,
)
