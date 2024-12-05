package com.lfssolutions.retialtouch.utils

expect class NumberFormatting() {
    fun format(value: Double, dec: Int = 2): String
}