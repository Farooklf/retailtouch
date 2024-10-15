package com.lfssolutions.retialtouch.presentation.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.LocalAppState

@Composable
fun AppScreenPadding(content: @Composable (horizontalPadding: Dp, verticalPadding: Dp) -> Unit) {
    ResponsivePadding(
        content = content
    )
}

@Composable
fun ResponsivePadding(
    content: @Composable (horizontalPadding: Dp, verticalPadding: Dp) -> Unit
) {
    val appState = LocalAppState.current
    val paddingValues = if (appState.isTablet) {
        AppTheme.dimensions.screenTabPadding to AppTheme.dimensions.screenTabPadding
    } else if (appState.isPortrait) {
        AppTheme.dimensions.screenPhonePortraitHorPadding to AppTheme.dimensions.screenPhonePortraitVerticalPadding
    } else {
        AppTheme.dimensions.screenPhoneLandHorPadding to AppTheme.dimensions.screenPhoneLandVerticalPadding
    }

    content(paddingValues.first, paddingValues.second)
}

