package com.lfssolutions.retialtouch.utils.printer

sealed class PaperSize(val value: Int) {
    data object Mm58 : PaperSize(1)
    data object Mm80 : PaperSize(2)

    // Function to get width based on paper size
    val width: Int
        get() = when (this) {
            Mm58 -> 372
            Mm80 -> 558
        }
}