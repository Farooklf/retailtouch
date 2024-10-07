package com.lfssolutions.retialtouch.domain.model.employee


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmployeesResult(
    @SerialName("items")
    val items: List<com.lfssolutions.retialtouch.domain.model.employee.EmployeesItem> = listOf()
)