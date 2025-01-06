package com.lfssolutions.retialtouch.domain.model.employee

import kotlinx.serialization.Serializable

@Serializable
data class POSEmployee(
    val employeeId: Int=0,
    val employeeCode: String="",
    val isDeleted: Boolean=false,
    val isPosEmployee: Boolean=false,
    val isAdmin: Boolean=false,
    val employeeName: String = "",
    val employeePassword: String = "",
    val employeeRoleName: String = "",
    val employeeDepartmentName: String = "",
    val employeeCategoryName: String = "",
    val creationTime: String = "",
    val permissions: List<String>? = listOf(),
    val grantedPermissionNames: List<String>? = listOf(),
    val restrictedPermissionNames: List<String>? = listOf()
)
