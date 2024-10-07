package com.lfssolutions.retialtouch.domain.model.menu


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuProductItem(
    @SerialName("backColor")
    val backColor: String?=null,
    @SerialName("barCode")
    val barCode: String?=null,
    @SerialName("creationTime")
    val creationTime: String="",
    @SerialName("creatorUserId")
    val creatorUserId: String?=null,
    @SerialName("deleterUserId")
    val deleterUserId: String?=null,
    @SerialName("deletionTime")
    val deletionTime: String?=null,
    @SerialName("foreColor")
    val foreColor: String?=null,
    @SerialName("id")
    val id: Int=0,
    @SerialName("imagePath")
    val imagePath: String?=null,
    @SerialName("inventoryCode")
    val inventoryCode: String="",
    @SerialName("isDeleted")
    val isDeleted: Boolean=false,
    @SerialName("lastModificationTime")
    val lastModificationTime: String?=null,
    @SerialName("lastModifierUserId")
    val lastModifierUserId: String?=null,
    @SerialName("menuCategoryId")
    val menuCategoryId: Int=0,
    @SerialName("name")
    val name: String="",
    @SerialName("productId")
    val productId: Int=0,
    @SerialName("sortOrder")
    val sortOrder: Int=0
)