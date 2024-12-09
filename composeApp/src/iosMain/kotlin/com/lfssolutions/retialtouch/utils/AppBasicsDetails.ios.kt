package com.lfssolutions.retialtouch.utils

actual class AppBasicsDetails actual constructor() {
    actual fun getAppName(): String {
        return NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleName") as? String ?: "Unknown"
    }
}