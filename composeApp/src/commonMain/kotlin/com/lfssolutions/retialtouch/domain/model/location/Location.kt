package com.lfssolutions.retialtouch.domain.model.location

import kotlinx.serialization.Serializable


@Serializable
data class Location(
    val locationId: Long = 0,
    val name: String = "",
    val code: String = "",
    val address1: String = "",
    val address2: String = "",
    val country: String = "",
    val isSelected: Boolean = false,
)
