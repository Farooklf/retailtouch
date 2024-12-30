package com.lfssolutions.retialtouch.utils

expect class NumberFormatter() {
    fun format(value: Double, dec: Int = 2): String
    fun formatAmountForPrint(amount: Double) : String
}