package com.lfssolutions.retialtouch.domain.model.members


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberResult(
    @SerialName("items")
    val items: List<MemberItem>? = listOf()
)