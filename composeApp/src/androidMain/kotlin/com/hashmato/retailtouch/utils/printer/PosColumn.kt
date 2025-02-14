package com.hashmato.retailtouch.utils.printer

class PosColumn(
    var text: String = "",
    var textEncoded: ByteArray? = null,
    var containsChinese: Boolean = false,
    var width: Int = 2,
    var styles: PosStyles = PosStyles()
) {

    init {
        if (width < 1 || width > 12) {
            throw IllegalArgumentException("Column width must be between 1..12")
        }
        if (text.isNotEmpty() && textEncoded != null && textEncoded!!.isNotEmpty()) {
            throw IllegalArgumentException("Only one parameter - text or textEncoded - should be passed")
        }
    }
}