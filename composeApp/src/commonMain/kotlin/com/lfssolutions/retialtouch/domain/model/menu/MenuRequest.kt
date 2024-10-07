package com.lfssolutions.retialtouch.domain.model.menu

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuRequest(
    @SerialName("id")
    val id: Int?=null,
    @SerialName("locationId")
    val locationId: Int?=null,
)
