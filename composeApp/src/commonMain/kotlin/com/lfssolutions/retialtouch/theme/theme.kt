package com.lfssolutions.retialtouch.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp


val LocalTypography = staticCompositionLocalOf { DesignTypography }
val LocalDimensions = staticCompositionLocalOf { DesignDimensions() }
val LocalColors = staticCompositionLocalOf { DesignColorsLight() }
val LocalAppShape = staticCompositionLocalOf {
    shape
}

object AppTheme{

    val colors
        @Composable
        @ReadOnlyComposable
        get() = if (isSystemInDarkTheme()) DesignColorsDark() else DesignColorsLight()

    val dimensions
        @Composable
        @ReadOnlyComposable
        get() = LocalDimensions.current

    val typography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current

    val appShape: AppShape
        @Composable
        get() = LocalAppShape.current


}

data class AppShape(
    val card: Shape,
    val cardRound: Shape,
    val button: Shape,
    val textField: Shape,
    val dialog: Shape,
)

private val shape = AppShape(
    card = RoundedCornerShape(8.dp),
    cardRound = RoundedCornerShape(100.dp),
    button = RoundedCornerShape(10.dp),
    textField = RoundedCornerShape(6.dp),
    dialog = RoundedCornerShape(10.dp),
)



@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {

    CompositionLocalProvider(
        LocalColors provides AppTheme.colors,
        LocalTypography provides AppTheme.typography,
        LocalDimensions provides AppTheme.dimensions,
        LocalAppShape provides AppTheme.appShape,
    ) {
        MaterialTheme(
            typography = DesignTypography.getMaterialTypography(),
            content = content
        )
    }
}