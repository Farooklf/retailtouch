package com.lfssolutions.retialtouch.utils.printer

data class PosTextSize(val value: Int) {

    companion object {
        val size1 = PosTextSize(1)
        val size2 = PosTextSize(2)
        val size3 = PosTextSize(3)
        val size4 = PosTextSize(4)
        val size5 = PosTextSize(5)
        val size6 = PosTextSize(6)
        val size7 = PosTextSize(7)
        val size8 = PosTextSize(8)

        // Function to calculate the decrement size
        fun decSize(height: PosTextSize, width: PosTextSize): Int {
            return 16 * (width.value - 1) + (height.value - 1)
        }
    }
}

