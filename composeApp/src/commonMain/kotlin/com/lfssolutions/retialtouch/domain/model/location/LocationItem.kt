package com.lfssolutions.retialtouch.domain.model.location


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationItem(
    @SerialName("address1")
    val address1: String? = null,
    @SerialName("address2")
    val address2: String? = null,
    @SerialName("address3")
    val address3: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("city")
    val city: String? = null,
    @SerialName("code")
    val code: String? = null,
    @SerialName("companyName")
    val companyName: String? = null,
    @SerialName("country")
    val country: String? = null,
    @SerialName("id")
    val id: Long? = null,
    @SerialName("state")
    val state: String? = null,
    @SerialName("menuId")
    val menuId: Long? = null,

)