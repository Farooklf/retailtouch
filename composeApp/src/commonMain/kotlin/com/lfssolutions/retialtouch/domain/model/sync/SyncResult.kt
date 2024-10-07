package com.lfssolutions.retialtouch.domain.model.sync


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SyncResult(
    @SerialName("items")
    val items: List<SyncItem> = listOf()
)