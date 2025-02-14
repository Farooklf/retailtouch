package com.hashmato.retailtouch.domain.model.terminal


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TerminalResult(
    @SerialName("items")
    val items: List<TerminalItem> = listOf()
)