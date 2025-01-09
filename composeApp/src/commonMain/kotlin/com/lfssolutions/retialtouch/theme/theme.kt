package com.lfssolutions.retialtouch.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.lfssolutions.retialtouch.domain.model.AppState
import com.lfssolutions.retialtouch.utils.AppConstants.LARGE_PHONE_MAX_WIDTH
import com.lfssolutions.retialtouch.utils.AppConstants.SMALL_PHONE_MAX_WIDTH
import com.lfssolutions.retialtouch.utils.AppConstants.SMALL_TABLET_MAX_WIDTH
import com.lfssolutions.retialtouch.utils.getScreenWidthHeight

sealed class Language(val isoFormat : String) {
    data object English : Language("en")
    data object Arabic : Language("ar")
    data object French : Language("fr")
    data object Chinese : Language("zh")
    data object Thai : Language("th")
    data object Japanese : Language("jp")
}

sealed class WindowSize(val size:Int){
    data class Small(val smallSize:Int):WindowSize(smallSize)
    data class Compact(val compactSize:Int):WindowSize(compactSize)
    data class Medium(val mediumSize:Int):WindowSize(mediumSize)
    data class Large(val largeSize:Int):WindowSize(largeSize)
}


data class WindowSizeClass(
    val width:WindowSize,
    val height:WindowSize
)
enum class Orientation {
    Portrait, Landscape
}

enum class Device {
    Tablet, Phone
}

data class AppShape(
    val card: Shape,
    val cardRound: Shape,
    val button: Shape,
    val textField: Shape,
    val dialog: Shape,
)

private val designAppShape = AppShape(
    card = RoundedCornerShape(8.dp),
    cardRound = RoundedCornerShape(100.dp),
    button = RoundedCornerShape(10.dp),
    textField = RoundedCornerShape(6.dp),
    dialog = RoundedCornerShape(10.dp),
)

val LocalTypography = staticCompositionLocalOf { DesignTypography }
val LocalDimensions = staticCompositionLocalOf { DesignDimensions() }
val LocalColors = staticCompositionLocalOf { designColorPaletteBlue() }
val LocalAppShape = staticCompositionLocalOf { designAppShape() }
val LocalOrientationMode = staticCompositionLocalOf { Orientation.Portrait }
val LocalLocalization = staticCompositionLocalOf { Language.English.isoFormat }
val LocalDeviceType = staticCompositionLocalOf { Device.Phone }

object AppTheme{

    val colors
        @Composable
        @ReadOnlyComposable
        get() = LocalColors.current

    val dimensions
        @Composable
        @ReadOnlyComposable
        get() = LocalDimensions.current

    val typography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current

    val appShape
        @Composable
        get() = LocalAppShape.current

    val appOrientation
        @Composable
        get() = LocalOrientationMode.current

    val appLanguage
        @Composable
        get() = LocalLocalization.current

    val deviceType
        @Composable
        get() = LocalDeviceType.current

}

@Composable
fun rememberWindowSizeClass(): WindowSizeClass {

    val screenWidthHeight = getScreenWidthHeight()
    println("screenWidthHeight $screenWidthHeight")
    val width=screenWidthHeight.first
    val height=screenWidthHeight.second

    val windowWidthClass = when {
        width <= 360 -> WindowSize.Small(width)
        width in 361..480 -> WindowSize.Compact(width)
        width in 481..720 -> WindowSize.Medium(width)
        else -> WindowSize.Large(width)
    }

    val windowHeightClass = when {
        height <= 360 -> WindowSize.Small(height)
        height in 361..480 -> WindowSize.Compact(height)
        height in 481..720 -> WindowSize.Medium(height)
        else -> WindowSize.Large(height)
    }

    return WindowSizeClass(windowWidthClass, windowHeightClass)

}



@Composable
fun AppTheme(
    windowSizeClass: WindowSizeClass = rememberWindowSizeClass(),
    language: String = Language.English.isoFormat,
    isAppDarkMode:Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colors = if(isAppDarkMode){
        designColorPaletteDark()
    }else{
        designColorPaletteBlue()
    }

    val orientation = when {
        windowSizeClass.width.size > windowSizeClass.height.size -> Orientation.Landscape
        else -> Orientation.Portrait
    }

    val deviceType = when(windowSizeClass.width) {
        is WindowSize.Small -> {
            Device.Phone
        }

        is WindowSize.Compact -> {
            Device.Phone
        }

        is WindowSize.Large -> {
            Device.Tablet
        }

        is WindowSize.Medium -> {
            Device.Tablet
        }
    }

    CompositionLocalProvider(
        LocalColors provides colors,
        LocalTypography provides AppTheme.typography,
        LocalDimensions provides AppTheme.dimensions,
        LocalAppShape provides AppTheme.appShape,
        LocalOrientationMode provides orientation,
        LocalDeviceType provides deviceType,
        LocalLocalization provides language,
    ) {
        MaterialTheme(
            typography = DesignTypography.getMaterialTypography(),
            content = content
        )
    }
}