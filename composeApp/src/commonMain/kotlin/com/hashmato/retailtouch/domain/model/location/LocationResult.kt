package com.hashmato.retailtouch.domain.model.location


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationResult(
    @SerialName("items")
    val items: List<LocationItem> = listOf()
)