package com.lfssolutions.retialtouch.domain.model.members

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Member(
    val memberId: Long = 0L,
    val postalCode: String = "",
    val address1: String = "",
    val address2: String = "",
    val address3: String = "",
    val memberCode: String = "",
    val locationName: String = "",
    val name: String = "",
    val mobileNo: String = "",
    val email: String = "",
    val active: Boolean = false,
){

    // Method to check if the product matches a given text
    fun matches(text: String): Boolean {
        if (text.isEmpty()) return true
        val searchText = text.lowercase()
        return (name.lowercase().contains(searchText)  ||
                memberCode.lowercase().contains(searchText))
    }
}
