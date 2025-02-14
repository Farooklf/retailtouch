package com.hashmato.retailtouch.domain.model.productBarCode


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductBarCodeItem(
    @SerialName("address1")
    val address1: String = "",
    @SerialName("address2")
    val address2: String = "",
    @SerialName("address3")
    val address3: String? = null,
    @SerialName("city")
    val city: String = "",
    @SerialName("code")
    val code: String = "",
    @SerialName("companyId")
    val companyId: Int = 0,
    @SerialName("companyName")
    val companyName: String? = null,
    @SerialName("country")
    val country: String = "",
    @SerialName("creationTime")
    val creationTime: String = "",
    @SerialName("creatorUserId")
    val creatorUserId: String? = null,
    @SerialName("deleterUserId")
    val deleterUserId: String? = null,
    @SerialName("deletionTime")
    val deletionTime: String? = null,
    @SerialName("email")
    val email: String = "",
    @SerialName("fax")
    val fax: String? = null,
    @SerialName("id")
    val id: Long = 0,
    @SerialName("isDeleted")
    val isDeleted: Boolean = false,
    @SerialName("isPurchaseAllowed")
    val isPurchaseAllowed: Boolean = false,
    @SerialName("isSalesAllowed")
    val isSalesAllowed: Boolean = false,
    @SerialName("iswareHouse")
    val iswareHouse: Boolean = false,
    @SerialName("lastModificationTime")
    val lastModificationTime: String = "",
    @SerialName("lastModifierUserId")
    val lastModifierUserId: Int = 0,
    @SerialName("locationGroupId")
    val locationGroupId: String? = null,
    @SerialName("menuId")
    val menuId: Int = 0,
    @SerialName("name")
    val name: String = "",
    @SerialName("phone")
    val phone: String? = null,
    @SerialName("remarks")
    val remarks: String? = null,
    @SerialName("state")
    val state: String? = null,
    @SerialName("website")
    val website: String? = null
)