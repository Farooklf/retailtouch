package com.lfssolutions.retialtouch.domain.model.menu

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDao(
    val categoryId:Long =0,
    val categoryItem: CategoryItem = CategoryItem(),
)

