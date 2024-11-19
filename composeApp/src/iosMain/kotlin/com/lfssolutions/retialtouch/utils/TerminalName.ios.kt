package com.lfssolutions.retialtouch.utils

actual fun getDeviceName(): String {
    return UIDevice.currentDevice.name
}