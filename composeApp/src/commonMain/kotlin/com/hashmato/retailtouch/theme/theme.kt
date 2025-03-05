package com.hashmato.retailtouch.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hashmato.retailtouch.domain.model.login.RTLoginUser
import com.hashmato.retailtouch.navigation.Route
import com.hashmato.retailtouch.navigation.toVoyagerScreen
import com.hashmato.retailtouch.utils.getScreenWidthHeight

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
    SMALL_TABLET,LARGE_TABLET, PHONE
}

data class AppShape(
    val card: Shape,
    val cardRound: Shape,
    val button: Shape,
    val textField: Shape,
    val dialog: Shape,
)


val LocalTypography = staticCompositionLocalOf { ComposeDesignTypography }
val LocalDimensions = staticCompositionLocalOf { ComposeDesignDimensions() }
val LocalColors = staticCompositionLocalOf { designColorPaletteBlue() }
val LocalAppShape = staticCompositionLocalOf { designAppShape() }
val LocalOrientationMode = staticCompositionLocalOf { Orientation.Portrait }
val LocalLocalization = staticCompositionLocalOf { Language.English.isoFormat }
val LocalDeviceType = staticCompositionLocalOf { Device.PHONE }
val LocalAppState = staticCompositionLocalOf { false }
val LocalAppOrientationMode = staticCompositionLocalOf { false }

data class AppThemeContext(
    val colors: ComposeDesignColors,
    val dimensions: ComposeDesignDimensions,
    val typography: ComposeDesignTypography,
    val appShape: ComposeDesignShape,
    val orientation: Orientation,
    val language: String,
    val deviceType: Device,
    val isTablet: Boolean,
    val isPortrait: Boolean,
){
    @Composable
    fun getAppNavigator():Navigator{
       return LocalNavigator.currentOrThrow
    }

    fun navigateBack(navigator: Navigator) {
        navigator.pop()
    }

    fun navigateBackToLoginScreen(navigator: Navigator,mRTUser: RTLoginUser?=null) {
        navigator.popUntilRoot() // Clear the back stack
        navigator.replace(Route.LoginScreen(mRTUser).toVoyagerScreen()) // Replace with Login screen
    }

    fun navigateBackToHomeScreen(navigator: Navigator,isSplash: Boolean) {
        navigator.popUntilRoot() // Clear the back stack
        navigator.replace(Route.HomeScreen(isSplash).toVoyagerScreen())
    }

    fun navigateToPayoutScreen(navigator: Navigator) {
        navigator.push(Route.Payout.toVoyagerScreen())
    }

    fun navigateToStockScreen(navigator: Navigator) {
        navigator.push(Route.Stock.toVoyagerScreen())
    }

    fun navigateToMemberScreen(navigator: Navigator) {
        navigator.push(Route.Member.toVoyagerScreen())
    }


}


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


    val isTablet
        @Composable
        get() = LocalAppState.current

    val isPortrait
        @Composable
        get() = LocalAppOrientationMode.current

    // Add this property
    val context: AppThemeContext
        @Composable
        get() = AppThemeContext(
            colors = colors,
            dimensions = dimensions,
            typography = typography,
            appShape = appShape,
            orientation = appOrientation,
            language = appLanguage,
            deviceType = deviceType,
            isTablet = isTablet,
            isPortrait = isPortrait,
        )

}

@Composable
fun rememberWindowSizeClass(): WindowSizeClass {

    val screenWidthHeight = getScreenWidthHeight()
    //println("screenWidthHeight $screenWidthHeight")
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


    val deviceType = getDeviceType(windowSizeClass)

    val isTablet=Device.PHONE != deviceType
    val isPortrait=orientation==Orientation.Portrait

    CompositionLocalProvider(
        LocalColors provides colors,
        LocalTypography provides AppTheme.typography,
        LocalDimensions provides AppTheme.dimensions,
        LocalAppShape provides AppTheme.appShape,
        LocalOrientationMode provides orientation,
        LocalDeviceType provides deviceType,
        LocalLocalization provides language,
        LocalAppState provides isTablet,
        LocalAppOrientationMode provides isPortrait
    ) {
        MaterialTheme(
            typography = ComposeDesignTypography.getMaterialTypography(),
            content = content
        )
    }
}

fun getDeviceType(windowSizeClass: WindowSizeClass): Device {
    return when (windowSizeClass.width) {
        is WindowSize.Small, is WindowSize.Compact -> Device.PHONE
        is WindowSize.Medium -> Device.SMALL_TABLET
        is WindowSize.Large -> Device.LARGE_TABLET
    }
}