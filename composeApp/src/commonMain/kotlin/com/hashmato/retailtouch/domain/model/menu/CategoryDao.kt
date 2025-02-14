package com.hashmato.retailtouch.domain.model.menu

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDao(
    val categoryId:Long =0,
    val stockCategory: StockCategory = StockCategory(),
)


