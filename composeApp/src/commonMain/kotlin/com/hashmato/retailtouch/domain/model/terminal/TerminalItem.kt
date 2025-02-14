package com.hashmato.retailtouch.domain.model.terminal


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TerminalItem(
    @SerialName("address1")
    val address1: String = "",
)