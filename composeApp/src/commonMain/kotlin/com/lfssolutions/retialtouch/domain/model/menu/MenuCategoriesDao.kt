package com.lfssolutions.retialtouch.domain.model.menu

import kotlinx.serialization.Serializable

@Serializable
data class MenuCategoriesDao(
    val categoryId:Long =0,
    val categoryItem: MenuCategoryItem = MenuCategoryItem(),
)


