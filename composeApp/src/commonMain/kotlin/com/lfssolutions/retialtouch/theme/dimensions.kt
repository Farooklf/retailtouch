package com.lfssolutions.retialtouch.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class DesignDimensions internal constructor(

    val horizontalPadding: Dp = 16.dp,
    val verticalPadding: Dp = 6.dp,
    val screenDefaultMaxWidth: Dp = 500.dp,
    val tabletDefaultWidth: Dp = 1000.dp,
    val tabPadding: Dp = 16.dp,
    val phonePadding: Dp = 10.dp,
    val progressWidth: Dp = 5.dp,
    val standerIcon: Dp = 24.dp,
    val smallIcon: Dp = 28.dp,
    val mediumIcon: Dp = 40.dp,
    val largeIcon: Dp = 70.dp,
    val cardElevation: Dp = 5.dp,

    val buttonHorizontalPadding: Dp = 20.dp,
    val buttonVerticalPadding: Dp = 10.dp,

    val buttonSquarePadding: Dp = 20.dp,

    val phoneListVerPadding: Dp = 10.dp,
    val tabListVerPadding: Dp = 16.dp,
    val listHorPadding: Dp = 5.dp,

    val tabVerPadding: Dp = 10.dp,
    val tabHorPadding: Dp = 20.dp,
    val phoneVerPadding: Dp = 10.dp,
    val phoneHorPadding: Dp = 10.dp,
)