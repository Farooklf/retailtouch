package com.lfssolutions.retialtouch.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import java.util.Locale

actual fun changeLang(lang: String) {
    val locale = Locale(lang)
    Locale.setDefault(locale)
}

@Composable
actual fun getScreenWidthHeight(): Pair<Int, Int> {
    val width = LocalConfiguration.current.screenWidthDp
    val height = LocalConfiguration.current.screenHeightDp
    return Pair(width, height)
}

actual fun exitApp() {
    android.os.Process.killProcess(android.os.Process.myPid())
}