package com.lfssolutions.retialtouch.domain.model

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lfssolutions.retialtouch.utils.DeviceType

data class AppState(
    val authenticated: Boolean = false,
    val isTablet: Boolean = false,
    val screenWidth: Dp = 0.dp,
    val deviceType: DeviceType = DeviceType.SMALL_PHONE, // Default to small phone
    val isPortrait:Boolean=false
)