package com.hashmato.retailtouch.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import kotlin.math.roundToInt

actual fun changeLang(lang: String) {
    NSUserDefaults.standardUserDefaults.setObject(arrayListOf(lang),"AppleLanguages")
}

actual fun getDeviceName(): String {
    return UIDevice.currentDevice.name
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getScreenWidthHeight(): Pair<Int, Int> {
    val screenWidth = with(LocalDensity.current) {
        LocalWindowInfo.current.containerSize.width.toDp()
    }

    val screenHeight = with(LocalDensity.current) {
        LocalWindowInfo.current.containerSize.height.toDp()
    }

    println("ios_config $screenWidth $screenHeight")

    return Pair(screenWidth.value.roundToInt(),screenHeight.value.roundToInt())
}

actual fun exitApp() {
    throw RuntimeException("Exit app requested") // Optional; generally discouraged in production
}

actual fun getAppName(): String {
    return NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleName") as? String ?: "Unknown"
}

actual fun getAppVersion(): String {
    return NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String ?: "Unknown"
}