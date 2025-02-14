package com.hashmato.retailtouch.domain.model.menu


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuResult(
    @SerialName("items")
    val items: List<MenuItem>
)