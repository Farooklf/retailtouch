package com.lfssolutions.retialtouch.utils

import kotlin.math.pow
import kotlin.math.round

actual class NumberFormatter actual constructor() {
    actual fun format(value: Double, dec: Int): String {
        val multiplier = 10.0.pow(dec)
        val roundedValue = round(value * multiplier) / multiplier
        return buildString {
            append(roundedValue.toString()) // Convert to String
            val parts = split(".")
            if (parts.size == 1) {
                // Add decimals if missing
                append(".${"0".repeat(dec)}")
            } else {
                // Add trailing zeros to match the required decimals
                append(parts[1].padEnd(dec, '0'))
            }
        }
    }

    actual fun formatAmountForPrint(amount: Double): String {
        return ""
    }
}