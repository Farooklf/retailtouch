package com.lfssolutions.retialtouch.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp


interface SystemDesignShape{
    val card: Shape
    val cardRound: Shape
    val button: Shape
    val textField: Shape
    val dialog: Shape
}

fun designAppShape() : SystemDesignShape = object : SystemDesignShape{
    override val card: Shape
        get() = RoundedCornerShape(8.dp)
    override val cardRound: Shape
        get() = RoundedCornerShape(100.dp)
    override val button: Shape
        get() = RoundedCornerShape(10.dp)
    override val textField: Shape
        get() = RoundedCornerShape(6.dp)
    override val dialog: Shape
        get() = RoundedCornerShape(10.dp)

}