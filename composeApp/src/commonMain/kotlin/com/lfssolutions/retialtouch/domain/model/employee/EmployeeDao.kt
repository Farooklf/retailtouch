package com.lfssolutions.retialtouch.domain.model.employee

data class EmployeeDao(
    val employeeId: Int=0,
    val employeeCode: String="",
    val isDeleted: Boolean=false,
    val isAdmin: Boolean=false,
    val employeeName: String = "",
    val employeePassword: String = "",
    val employeeRoleName: String = "",
    val employeeDepartmentName: String = "",
    val employeeCategoryName: String = "",
    val creationTime: String = "",
)
