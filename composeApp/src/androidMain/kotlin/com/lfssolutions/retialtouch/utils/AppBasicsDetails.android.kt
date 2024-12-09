package com.lfssolutions.retialtouch.utils

import com.lfssolutions.retialtouch.AndroidApp.Companion.getApplicationContext

actual class AppBasicsDetails actual constructor() {
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
}