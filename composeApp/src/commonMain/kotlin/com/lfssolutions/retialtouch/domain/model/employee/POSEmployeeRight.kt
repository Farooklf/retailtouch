package com.lfssolutions.retialtouch.domain.model.employee

import kotlinx.serialization.Serializable

@Serializable
data class POSEmployeeRight(
    val id: Int=0,
    val name: String="",
    val isAdmin: Boolean=false,
    val permissions: List<String>? = listOf(),
    val grantedPermissionNames: List<String>? = listOf(),
    val restrictedPermissionNames: List<String>? = listOf()
)
