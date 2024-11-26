package com.lfssolutions.retialtouch.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.DeviceType
import com.lfssolutions.retialtouch.utils.LocalAppState

@Composable
fun ResponsiveBox(
    modifier:Modifier=Modifier.fillMaxSize(),
    bgColor:Color=AppTheme.colors.backgroundWindow,
    isForm:Boolean=false,
    content: @Composable BoxScope.() -> Unit
){
    BoxWithConstraints(
        modifier = modifier
            .background(bgColor)
    ){
        val appState = LocalAppState.current

        val boxWidth = when(appState.deviceType){
            DeviceType.SMALL_PHONE -> maxWidth
            DeviceType.SMALL_TABLET -> maxWidth/1
            DeviceType.LARGE_PHONE -> maxWidth/1
            DeviceType.LARGE_TABLET -> if(isForm)maxWidth/2 else maxWidth/1
        }
        val paddingValues = if (appState.isTablet) {
            AppTheme.dimensions.padding20 to AppTheme.dimensions.padding10
        } else{
            AppTheme.dimensions.padding10 to AppTheme.dimensions.padding10
        }
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center){
            Box(
                modifier = Modifier
                    .width(boxWidth)
                    .fillMaxHeight()
                    .padding(horizontal = paddingValues.first, vertical = paddingValues.second),
                contentAlignment = Alignment.Center)
            {
                content()
            }
        }
    }
}

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

