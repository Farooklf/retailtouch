package com.lfssolutions.retialtouch.utils

import android.os.Build

actual fun getDeviceName(): String {
    return Build.MANUFACTURER + Build.PRODUCT
}