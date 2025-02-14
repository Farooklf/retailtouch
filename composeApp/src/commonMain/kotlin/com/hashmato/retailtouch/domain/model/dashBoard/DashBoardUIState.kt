package com.hashmato.retailtouch.domain.model.dashBoard

data class DashBoardUIState(
    val isLoading: Boolean = false,
    val isDashBoardBlur: Boolean = true,
    val isShowEmployeeScreen: Boolean = false,
    val hasEmployeeLoggedIn: Boolean = false // Default to false
)
