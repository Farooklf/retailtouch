package com.lfssolutions.retialtouch.utils

import androidx.compose.runtime.Composable


@Composable
expect fun getScreenWidthHeight(): Pair<Int, Int>
expect fun changeLang(lang: String)
expect fun getDeviceName(): String
expect fun getAppName(): String
expect fun exitApp()