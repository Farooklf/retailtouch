package com.hashmato.retailtouch.domain.model.employee


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmployeesItem(
    @SerialName("creationTime")
    val creationTime: String = "",
    @SerialName("creatorUserId")
    val creatorUserId: String? = null,
    @SerialName("deleterUserId")
    val deleterUserId: String? = null,
    @SerialName("deletionTime")
    val deletionTime: String? = null,
    @SerialName("employeeCategoryId")
    val employeeCategoryId: String? = null,
    @SerialName("employeeCategoryName")
    val employeeCategoryName: String? = null,
    @SerialName("employeeCode")
    val employeeCode: String = "",
    @SerialName("employeeDepartmentId")
    val employeeDepartmentId: String? = null,
    @SerialName("employeeDepartmentName")
    val employeeDepartmentName: String? = null,
    @SerialName("employeeRoleId")
    val employeeRoleId: Int = 0,
    @SerialName("employeeRoleName")
    val employeeRoleName: String = "",
    @SerialName("group")
    val group: Boolean = false,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("isAdmin")
    val isAdmin: Boolean = false,
    @SerialName("isDeleted")
    val isDeleted: Boolean = false,
    @SerialName("isSalesman")
    val isSalesman: Boolean = false,
    @SerialName("lastModificationTime")
    val lastModificationTime: String? = null,
    @SerialName("lastModifierUserId")
    val lastModifierUserId: String? = null,
    @SerialName("locations")
    val locations: String? = null,
    @SerialName("name")
    val name: String = "",
    @SerialName("password")
    val password: String = ""
)