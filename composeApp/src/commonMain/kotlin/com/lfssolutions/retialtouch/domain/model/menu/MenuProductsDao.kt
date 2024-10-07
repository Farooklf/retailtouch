package com.lfssolutions.retialtouch.domain.model.menu

import kotlinx.serialization.Serializable

@Serializable
data class MenuProductsDao(
    val productId:Long =0,
    val menuProductItem: MenuProductItem = MenuProductItem()
)


