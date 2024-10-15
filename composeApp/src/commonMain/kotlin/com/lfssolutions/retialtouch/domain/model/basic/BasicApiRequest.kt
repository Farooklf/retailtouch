package com.lfssolutions.retialtouch.domain.model.basic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BasicApiRequest(
    @SerialName("tenantId")
    val tenantId: Int?=null,
    @SerialName("locationId")
    val locationId: Int?=null,
    @SerialName("id")
    val id: Int?=null,
    @SerialName("LastSyncDateTime")
    val lastSyncDateTime: String? = null,
    @SerialName("isDeleted")
    val isDeleted: Boolean? = null,
)
