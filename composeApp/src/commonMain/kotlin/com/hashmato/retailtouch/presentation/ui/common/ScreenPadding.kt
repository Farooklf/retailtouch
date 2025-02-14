package com.hashmato.retailtouch.presentation.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.hashmato.retailtouch.theme.AppTheme
import com.hashmato.retailtouch.theme.Device
import com.hashmato.retailtouch.utils.LocalAppState

@Composable
fun ResponsiveBox(
    modifier:Modifier=Modifier.fillMaxSize(),
    isForm:Boolean=false,
    content: @Composable BoxScope.() -> Unit
){

    BoxWithConstraints(
        modifier = modifier
    ){
        val appThemeContext = AppTheme.context

        //val appState = LocalAppState.current

        val boxWidth = when(appThemeContext.deviceType){
            Device.PHONE -> maxWidth
            Device.SMALL_TABLET -> maxWidth/1
            Device.LARGE_TABLET -> if(isForm) maxWidth/2 else maxWidth/1
        }
        val paddingValues = if (appThemeContext.isTablet) {
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

