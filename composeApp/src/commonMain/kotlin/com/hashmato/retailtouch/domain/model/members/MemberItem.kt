package com.hashmato.retailtouch.domain.model.members


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MemberItem(
    @SerialName("active")
    val active: Boolean? = false,
    @SerialName("address1")
    val address1: String? = null,
    @SerialName("address2")
    val address2: String? = null,
    @SerialName("address3")
    val address3: String? = null,
    @SerialName("attachment")
    val attachment: String? = null,
    @SerialName("balancePoints")
    val balancePoints: Double? = 0.0,
    @SerialName("birthDate")
    val birthDate: String? = null,
    @SerialName("city")
    val city: String? = null,
    @SerialName("country")
    val country: String? = null,
    @SerialName("createdDate")
    val createdDate: String? = null,
    @SerialName("creationTime")
    val creationTime: String? = "",
    @SerialName("creatorUserId")
    val creatorUserId: Int? = 0,
    @SerialName("deleterUserId")
    val deleterUserId: String? = null,
    @SerialName("deletionTime")
    val deletionTime: String? = null,
    @SerialName("earnedPoints")
    val earnedPoints: Double? = 0.0,
    @SerialName("email")
    val email: String? = null,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("isDeleted")
    val isDeleted: Boolean? = false,
    @SerialName("joinDate")
    val joinDate: String? = "",
    @SerialName("lastModificationTime")
    val lastModificationTime: String? = null,
    @SerialName("lastModifierUserId")
    val lastModifierUserId: String? = null,
    @SerialName("locationCode")
    val locationCode: String? = null,
    @SerialName("locationId")
    val locationId: Int? = 0,
    @SerialName("locationName")
    val locationName: String? = null,
    @SerialName("memberCode")
    val memberCode: String? = "",
    @SerialName("memberDepartment")
    val memberDepartment: String? = null,
    @SerialName("memberDepartmentId")
    val memberDepartmentId: String? = null,
    @SerialName("memberGroup")
    val memberGroup: String? = null,
    @SerialName("memberGroupId")
    val memberGroupId: Int? = 0,
    @SerialName("memberSubDepartment")
    val memberSubDepartment: String? = null,
    @SerialName("memberSubDepartmentId")
    val memberSubDepartmentId: String? = null,
    @SerialName("memberTitle")
    val memberTitle: String? = null,
    @SerialName("memberTitleId")
    val memberTitleId: String? = null,
    @SerialName("mobileNo")
    val mobileNo: String? = null,
    @SerialName("name")
    val name: String = "",
    @SerialName("postalCode")
    val postalCode: String? = null,
    @SerialName("priceGroupId")
    val priceGroupId: String? = null,
    @SerialName("remarks")
    val remarks: String? = null,
    @SerialName("state")
    val state: String? = null,
    @SerialName("totalPurchase")
    val totalPurchase: Double? = 0.0,
    @SerialName("usedPoints")
    val usedPoints: Double? = 0.0
)