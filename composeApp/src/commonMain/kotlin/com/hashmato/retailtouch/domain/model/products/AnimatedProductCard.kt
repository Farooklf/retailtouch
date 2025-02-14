package com.hashmato.retailtouch.domain.model.products

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class AnimatedProductCard(
    val product: Stock,
    val width: Dp = 0.dp,
    val height: Dp = 0.dp,
    val xOffset: Dp = 0.dp,
    val yOffset: Dp = 0.dp
)
