package com.lfssolutions.retialtouch.domain.model.menu


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryItem(
    @SerialName("backColor")
    val backColor: String? = null,
    @SerialName("categoryGroupHeight")
    val categoryGroupHeight: Int = 0,
    @SerialName("categoryRowCount")
    val categoryRowCount: Int? = 0,
    @SerialName("columnHeight")
    val columnHeight: Int = 0,
    @SerialName("columnWidth")
    val columnWidth: Int = 0,
    @SerialName("creationTime")
    val creationTime: String = "",
    @SerialName("creatorUserId")
    val creatorUserId: String? = null,
    @SerialName("deleterUserId")
    val deleterUserId: String? = null,
    @SerialName("deletionTime")
    val deletionTime: String? = null,
    @SerialName("fontSize")
    val fontSize: Double = 0.0,
    @SerialName("foreColor")
    val foreColor: String? = null,
    @SerialName("id")
    val id: Int = 0,
    @SerialName("isDeleted")
    val isDeleted: Boolean = false,
    @SerialName("itemBackColor")
    val itemBackColor: String? = null,
    @SerialName("itemColumCount")
    val itemColumCount: Int = 0,
    @SerialName("itemFontSize")
    val itemFontSize: Double = 0.0,
    @SerialName("itemForeColor")
    val itemForeColor: String? = null,
    @SerialName("itemHeight")
    val itemHeight: Int = 0,
    @SerialName("itemRowCount")
    val itemRowCount: Int = 0,
    @SerialName("itemWidth")
    val itemWidth: Int = 0,
    @SerialName("lastModificationTime")
    val lastModificationTime: String? = null,
    @SerialName("lastModifierUserId")
    val lastModifierUserId: String? = null,
    @SerialName("menuId")
    val menuId: Int = 0,
    @SerialName("name")
    val name: String = "",
    @SerialName("sortOrder")
    val sortOrder: Int = 0
)