package com.lfssolutions.retialtouch.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lfssolutions.retialtouch.theme.AppTheme

@Composable
fun GradientBackgroundScreen(
    modifier: Modifier = Modifier,
    blur: Float = .1f,
    screenBackground: Brush = AppTheme.colors.screenGradientHorizontalBg,
    contentMaxWidth: Dp = AppTheme.dimensions.screenDefaultMaxWidth,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 20.dp,
        vertical = 20.dp,
    ),
    isBlur: Boolean = true,
    content: @Composable () -> Unit = {},
){

    BoxWithConstraints(
        modifier = modifier
            .background(screenBackground)
            .windowInsetsPadding(WindowInsets.systemBars)
            .alpha(if(isBlur) blur else 1f)
    ) {
        content()
    }
}


private val LocalScreenHeight = staticCompositionLocalOf { Dp.Unspecified }

/**
 * Sets the screen's to the minimum height to be the full height. This is useful
 * for allowing screen scrolling while also have buttons anchored to the bottom.
 */
fun Modifier.fillScreenHeight() = composed {
    heightIn(min = LocalScreenHeight.current)
}

fun PaddingValues.verticalPadding(): Dp {
    return calculateTopPadding() + calculateBottomPadding()
}