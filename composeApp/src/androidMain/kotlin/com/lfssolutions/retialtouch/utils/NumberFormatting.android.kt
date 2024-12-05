package com.lfssolutions.retialtouch.utils

import java.text.DecimalFormat

actual class NumberFormatting actual constructor() {
    actual fun format(value: Double, dec: Int): String {
        val pattern = "0.${"0".repeat(dec)}" // Create a pattern like "0.00"
        val decimalFormat = DecimalFormat(pattern)
        return decimalFormat.format(value)
    }
}