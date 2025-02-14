package com.hashmato.retailtouch.domain.model.sync

import kotlinx.serialization.Serializable


@Serializable
data class SyncAllDao(
    val syncId: Long = 0L,
    val rowItem: SyncItem = SyncItem(),
)
