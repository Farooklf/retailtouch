package com.hashmato.retailtouch.utils

expect class NumberFormatter() {
    fun format(value: Double, dec: Int = 2): String
    fun formatAmountForPrint(amount: Double) : String
}