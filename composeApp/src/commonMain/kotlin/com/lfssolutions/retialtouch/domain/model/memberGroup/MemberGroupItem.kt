package com.lfssolutions.retialtouch.domain.model.memberGroup


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberGroupItem(
    @SerialName("cashbackPromotion")
    val cashbackPromotion: Double? = 0.0,
    @SerialName("cashbackRegular")
    val cashbackRegular: Double? = 0.0,
    @SerialName("creationTime")
    val creationTime: String? = "",
    @SerialName("creatorUserId")
    val creatorUserId: Int? = 0,
    @SerialName("deleterUserId")
    val deleterUserId: String? = null,
    @SerialName("deletionTime")
    val deletionTime: String? = null,
    @SerialName("discountPercentage")
    val discountPercentage: Double? = 0.0,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("isDefault")
    val isDefault: Boolean? = false,
    @SerialName("isDeleted")
    val isDeleted: Boolean? = false,
    @SerialName("lastModificationTime")
    val lastModificationTime: String? = "",
    @SerialName("lastModifierUserId")
    val lastModifierUserId: Int? = 0,
    @SerialName("monthlyQuota")
    val monthlyQuota: Double? = 0.0,
    @SerialName("name")
    val name: String? = "",
    @SerialName("pointsAmount")
    val pointsAmount: Double? = 0.0,
    @SerialName("pointsPerAmount")
    val pointsPerAmount: Double? = 0.0,
    @SerialName("priceGroupId")
    val priceGroupId: String? = null
)