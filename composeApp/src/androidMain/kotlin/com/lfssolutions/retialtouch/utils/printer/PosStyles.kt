package com.lfssolutions.retialtouch.utils.printer

data class PosStyles(
    val bold: Boolean = false,
    val reverse: Boolean = false,
    val underline: Boolean = false,
    val turn90: Boolean = false,
    val align: PosAlign = PosAlign.LEFT,
    val height: PosTextSize = PosTextSize.size1,
    val width: PosTextSize = PosTextSize.size1,
    val fontType: PosFontType? = PosFontType.FONT_A,
    val codeTable: String? = "CP437"
)

