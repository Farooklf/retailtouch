package com.lfssolutions.retialtouch.utils

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

actual class NumberFormatter actual constructor() {
    actual fun format(value: Double, dec: Int): String {
        val pattern = "0.${"0".repeat(dec)}" // Create a pattern like "0.00"
        val decimalFormat = DecimalFormat(pattern)
        return decimalFormat.format(value)
    }

    actual fun formatAmountForPrint(amount: Double): String {
        val formatter = NumberFormat.getInstance(Locale.US)
        formatter.minimumFractionDigits = 2
        formatter.maximumFractionDigits = 2
        return formatter.format(amount)
    }
}