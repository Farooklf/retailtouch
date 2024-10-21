package com.lfssolutions.retialtouch.domain.model.employee


import com.lfssolutions.retialtouch.domain.model.ErrorResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmployeesRights(
    @SerialName("error")
    val error: ErrorResponse?,
    @SerialName("result")
    val result: RTEmployeeRightList = RTEmployeeRightList(),
    @SerialName("success")
    val success: Boolean = false
)

@Serializable
data class RTEmployeeRightList(
    @SerialName("items")
    val items: List<RTEmployeeRight> = listOf()
)

@Serializable
data class RTEmployeeRight(
    @SerialName("employeeRole")
    val employeeRole: RTEmployeeRole? = null,
    @SerialName("permissions")
    val permissions: List<String>? = listOf(),
    @SerialName("grantedPermissionNames")
    val grantedPermissionNames: List<String>? = listOf(),
    @SerialName("restrictedPermissionNames")
    val restrictedPermissionNames: List<String>? = listOf()
)

@Serializable
data class RTEmployeeRole(
    @SerialName("isAdmin")
    val isAdmin: Boolean = false,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("creatorUserId")
    val creatorUserId: Int? = null,
    @SerialName("creationTime")
    val creationTime: String? = null,
    @SerialName("tenantId")
    val tenantId: Int? = null,
    @SerialName("name")
    val name: String = "",
    @SerialName("isDeleted")
    val isDeleted: Boolean = false
)