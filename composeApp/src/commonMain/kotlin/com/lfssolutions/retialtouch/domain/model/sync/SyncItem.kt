package com.lfssolutions.retialtouch.domain.model.sync


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SyncItem(
    @SerialName("id")
    val id: Int = 0,
    @SerialName("name")
    val name: String = "",
    @SerialName("syncerGuid")
    val syncerGuid: String = ""
)