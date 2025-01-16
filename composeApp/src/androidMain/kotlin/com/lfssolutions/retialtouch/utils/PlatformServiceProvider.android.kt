package com.lfssolutions.retialtouch.utils

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import com.lfssolutions.retialtouch.AndroidApp.Companion.getApplicationContext
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

actual fun getDeviceName(): String {
    return Build.MANUFACTURER + Build.PRODUCT
}
actual fun getAppName(): String {
    val context = getApplicationContext()
    val applicationInfo = context.applicationInfo
    val stringId = applicationInfo.labelRes
    return if (stringId == 0) {
        applicationInfo.nonLocalizedLabel.toString()
    } else {
        context.getString(stringId)
    }
}
actual fun exitApp() {
    android.os.Process.killProcess(android.os.Process.myPid())
}