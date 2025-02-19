package com.hashmato.retailtouch.domain.model.memberGroup


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberGroupResult(
    @SerialName("items")
    val items: List<MemberGroupItem>? = listOf()
)