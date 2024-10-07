package com.lfssolutions.retialtouch.domain.model.location


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationResult(
    @SerialName("items")
    val items: List<com.lfssolutions.retialtouch.domain.model.location.LocationItem> = listOf()
)