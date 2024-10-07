package com.lfssolutions.retialtouch.domain.model.menu


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuProductResult(
    @SerialName("items")
    val items: List<com.lfssolutions.retialtouch.domain.model.menu.MenuProductItem>
)