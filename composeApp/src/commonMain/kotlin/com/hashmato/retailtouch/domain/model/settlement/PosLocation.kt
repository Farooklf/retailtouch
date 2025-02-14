package com.hashmato.retailtouch.domain.model.settlement

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class PosLocation(
    @SerialName("id")
    val id: String? = "",
    @SerialName("name")
    val name: String? = "",
    @SerialName("code")
    val code: String? = "",
)
