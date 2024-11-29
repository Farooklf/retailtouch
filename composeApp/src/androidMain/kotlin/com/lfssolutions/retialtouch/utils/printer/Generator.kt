package com.lfssolutions.retialtouch.utils.printer

import java.nio.charset.Charset
import kotlin.math.roundToInt



// Define the escape sequences
const val ESC = '\u001B'  // ESC (ASCII 27)
const val GS = '\u001D'   // GS (ASCII 29)
const val FS = '\u001C'   // FS (ASCII 28)

const val cPos = "$ESC\$" // Set absolute print position [nL] [nH]
const val C_ALIGN_LEFT = "${ESC}a0"
const val C_ALIGN_CENTER = "${ESC}a1" // Center alignment
const val C_ALIGN_RIGHT = "${ESC}a2"  // Right justification

const val cReverseOn = "${GS}B\\x01" // Turn white/black reverse print mode on
const val cReverseOff = "${GS}B\\x00" // Turn white/black reverse print mode off
const val cSizeGSn = "$GS!" // Select character size [N]
const val cSizeESCn = "$ESC!" // Select character size [N]
const val cUnderlineOff = "$ESC-\\x00" // Turns off underline mode
const val cUnderline1dot = "$ESC-\\x01" // Turns on underline mode (1-dot thick)
const val cUnderline2dots = "$ESC-\\x02" // Turns on underline mode (2-dots thick)
const val cBoldOn = "${ESC}E\\x01"
const val cBoldOff = "${ESC}E\\x00"
const val cFontA = "${ESC}M\\x00" // Font A
const val cFontB = "${ESC}M\\x01" // Font B
const val cTurn90On = "${ESC}V\\x01" // Turn 90° clockwise rotation mode on
const val cTurn90Off = "${ESC}V\\x00"// Turn 90° clockwise rotation mode off
const val cCodeTable = "${ESC}t" // Select character code table [N]
const val cKanjiOn = "$FS&" // Select Kanji character mode
const val cKanjiOff = "$FS." // Cancel Kanji character mode

class Generator(
    private val _paperSize: PaperSize,
    val spaceBetweenRows: Int = 5
) {
    private var _maxCharsPerLine: Int? = null

    // Global styles
    var _codeTable: String? = null
    var _font: PosFontType? = null

    // Current styles
    var _styles: PosStyles = PosStyles()



    fun _getMaxCharsPerLine(font: PosFontType?): Int {
        return if (_paperSize == PaperSize.Mm58) {
            if (font == null || font == PosFontType.FONT_A) 32 else 42
        } else {
            if (font == null || font == PosFontType.FONT_A) 48 else 64
        }
    }

    fun _getCharWidth(styles: PosStyles, maxCharsPerLine: Int? = null): Double {
        val charsPerLine = _getCharsPerLine(styles, maxCharsPerLine)
        val charWidth = (_paperSize.width.toDouble() / charsPerLine) * styles.width.value
        return charWidth
    }

    fun _getCharsPerLine(styles: PosStyles, maxCharsPerLine: Int?): Int {
        val charsPerLine: Int = maxCharsPerLine
            ?: if (styles.fontType != null) {
                _getMaxCharsPerLine(styles.fontType)
            } else {
                _maxCharsPerLine ?: _getMaxCharsPerLine(_styles.fontType)
            }
        return charsPerLine
    }

    private fun _colIndToPosition(colInd: Int): Double {
        val width = _paperSize.width
        return if (colInd == 0) 0.0 else (width * colInd / 12 - 1).toDouble()
    }


    fun row(cols: List<PosColumn>): StringBuilder {
        //val bytes = mutableListOf<Byte>()
        val stringBuilder = StringBuilder()
        // Validate that total column width is equal to 12
        val isSumValid = cols.fold(0) { sum, col -> sum + col.width } == 12
        if (!isSumValid) {
            throw Exception("Total columns width must be equal to 12")
        }

        var isNextRow = false
        val nextRow = mutableListOf<PosColumn>()

        // Process each column
        for (i in cols.indices) {
            val colInd = cols.subList(0, i).sumOf { it.width }
            val charWidth = _getCharWidth(cols[i].styles)
            val fromPos = _colIndToPosition(colInd)
            val toPos = _colIndToPosition(colInd + cols[i].width) - spaceBetweenRows
            val maxCharactersNb = ((toPos - fromPos) / charWidth).toInt()

            if (!cols[i].containsChinese) {
                // CASE 1: containsChinese = false
                var encodedToPrint = cols[i].textEncoded ?: encode(cols[i].text)

                // If the column's content is too long, split it to the next row
                val realCharactersNb = encodedToPrint.size
                if (realCharactersNb > maxCharactersNb) {
                    // Print max possible and split to the next row
                    val encodedToPrintNextRow = encodedToPrint.copyOfRange(maxCharactersNb, realCharactersNb)
                    encodedToPrint = encodedToPrint.copyOfRange(0, maxCharactersNb)
                    isNextRow = true
                    nextRow.add(PosColumn(textEncoded = encodedToPrintNextRow, width = cols[i].width, styles = cols[i].styles))
                } else {
                    // Insert an empty column
                    nextRow.add(PosColumn("", width = cols[i].width, styles = cols[i].styles))
                }

                // Add the text to bytes
                // Add the text to string
                stringBuilder.append(_text(encodedToPrint, styles = cols[i].styles, colInd, colWidth = cols[i].width))
                //bytes.addAll(_text(encodedToPrint, styles = cols[i].styles, colInd, colWidth = cols[i].width))
            }
            else {
                // CASE 2: containsChinese = true
                // Split text into multiple lines if it's too long
                var counter = 0
                var splitPos = 0
                for (p in 0 until cols[i].text.length) {
                    val w = if (isChinese(cols[i].text[p])) 2 else 1
                    if (counter + w >= maxCharactersNb) {
                        break
                    }
                    counter += w
                    splitPos += 1
                }
                val toPrintNextRow = cols[i].text.substring(splitPos)
                val toPrint = cols[i].text.substring(0, splitPos)

                if (toPrintNextRow.isNotEmpty()) {
                    isNextRow = true
                    nextRow.add(PosColumn(toPrintNextRow, containsChinese = true, width = cols[i].width, styles = cols[i].styles))
                } else {
                    // Insert an empty column
                    nextRow.add(PosColumn("", width = cols[i].width, styles = cols[i].styles))
                }

                // Process lexemes and print each lexeme
                val list = getLexemes(toPrint)
                val lexemes = list.first
                val isLexemeChinese = list.second

                for (j in lexemes.indices) {
                    //bytes.addAll(_text( encode(lexemes[j], isKanji = isLexemeChinese[j]), cols[i].styles, colInd, colWidth = cols[i].width, isKanji = isLexemeChinese[j]))
                    stringBuilder.append(_text( encode(lexemes[j], isKanji = isLexemeChinese[j]), cols[i].styles, colInd, colWidth = cols[i].width, isKanji = isLexemeChinese[j]))
                }
            }
        }

        // Add empty lines if needed
        //bytes.addAll(emptyLines(1))
        stringBuilder.append(emptyLines(1))

        if (isNextRow) {
            // Recursive call to process the next row
            //bytes.addAll(row(nextRow))
            stringBuilder.append(row(nextRow))
        }

        return stringBuilder
    }


    fun calculateRow(cols: List<PosColumn>): StringBuilder {
        val stringBuilder = StringBuilder()

        // Validate that total column width is equal to 12
        val isSumValid = cols.fold(0) { sum, col -> sum + col.width } == 12
        if (!isSumValid) {
            throw Exception("Total columns width must be equal to 12")
        }

        var isNextRow = false
        val nextRow = mutableListOf<PosColumn>()

        // Process each column
        for (i in cols.indices) {
            val colInd = cols.subList(0, i).sumOf { it.width }
            val charWidth = _getCharWidth(cols[i].styles)
            val fromPos = _colIndToPosition(colInd)
            val toPos = _colIndToPosition(colInd + cols[i].width) - spaceBetweenRows
            val maxCharactersNb = ((toPos - fromPos) / charWidth).toInt()

            if (!cols[i].containsChinese) {
                // CASE 1: containsChinese = false
                var encodedToPrint = cols[i].text

                // If the column's content is too long, split it to the next row
                val realCharactersNb = encodedToPrint.length
                if (realCharactersNb > maxCharactersNb) {
                    val toPrintNextRow = encodedToPrint.substring(maxCharactersNb)
                    encodedToPrint = encodedToPrint.substring(0, maxCharactersNb)
                    isNextRow = true
                    nextRow.add(PosColumn(text = toPrintNextRow, width = cols[i].width, styles = cols[i].styles))
                } else {
                    nextRow.add(PosColumn("", width = cols[i].width, styles = cols[i].styles))
                }

                // Add the text to the string with styles
                stringBuilder.append(
                    calculateRowSpacing(
                        text = encodedToPrint,
                        styles = cols[i].styles,
                        colInd = colInd,
                        colWidth = cols[i].width
                    )
                )
            } else {
                // CASE 2: containsChinese = true
                var counter = 0
                var splitPos = 0
                for (p in cols[i].text.indices) {
                    val w = if (isChinese(cols[i].text[p])) 2 else 1
                    if (counter + w >= maxCharactersNb) {
                        break
                    }
                    counter += w
                    splitPos += 1
                }

                val toPrintNextRow = cols[i].text.substring(splitPos)
                val toPrint = cols[i].text.substring(0, splitPos)

                if (toPrintNextRow.isNotEmpty()) {
                    isNextRow = true
                    nextRow.add(PosColumn(toPrintNextRow, containsChinese = true, width = cols[i].width, styles = cols[i].styles))
                } else {
                    nextRow.add(PosColumn("", width = cols[i].width, styles = cols[i].styles))
                }

                // Process and append lexemes with styles
                val (lexemes, isLexemeChinese) = getLexemes(toPrint)
                for (j in lexemes.indices) {
                    stringBuilder.append(
                        calculateRowSpacing(
                            text = lexemes[j],
                            styles = cols[i].styles,
                            colInd = colInd,
                            colWidth = cols[i].width,
                            isKanji = isLexemeChinese[j]
                        )
                    )
                }
            }
        }

        // Add empty lines if needed
        //stringBuilder.append(emptyLines(1))
        //stringBuilder.append("\n")

        if (isNextRow) {
            // Recursive call to process the next row
            stringBuilder.append(calculateRow(nextRow))
        }

        return stringBuilder
    }


    private fun calculateRowSpacing(
        text: String,
        styles: PosStyles = PosStyles(),
        colInd: Int? = 0,
        colWidth: Int = 12,
        isKanji: Boolean = false,
        maxCharsPerLine: Int? = null
    ): String {
        //println("colWidth $colWidth")
        //println("text $text")

        if(colInd!=null){
            // Calculate char width
            val charWidth = _getCharWidth(styles, maxCharsPerLine = maxCharsPerLine)
            var fromPos = _colIndToPosition(colInd)
            var alignedText = text

            if(colWidth!=12){
                val toPos=_colIndToPosition(colInd + colWidth) - spaceBetweenRows
                val textLen= text.length * charWidth

                if (styles.align == PosAlign.RIGHT){
                    fromPos = toPos - textLen
                }else if (styles.align == PosAlign.CENTER) {
                    fromPos = fromPos + (toPos - fromPos) / 2 - textLen / 2
                }

                // Ensure fromPos is not negative
                if (fromPos < 0) fromPos = 0.0
            }

            // Calculate padding based on alignment
            val leftPadding = (fromPos / charWidth).toInt()
            alignedText = " ".repeat(leftPadding) + text

            // Apply styles like <b>, <u>, etc.
            val styledText = buildString {
                if (styles.bold) append("<b>")
                if (styles.underline) append("<u>")
                if (styles.reverse) append("<i>")
                append(alignedText)
                if (styles.reverse) append("</i>")
                if (styles.underline) append("</u>")
                if (styles.bold) append("</b>")
            }
            return styledText
        }
        return text
    }


    private fun encode(text: String, isKanji: Boolean = false): ByteArray {
        // Replace some non-ASCII characters as in the original Dart code
        val updatedText = text
            .replace("’", "'")
            .replace("´", "'")
            .replace("»", "\"")
            .replace(" ", " ") // Non-breaking space replaced by regular space
            .replace("•", ".")

        return if (!isKanji) {
            // Latin-1 encoding (ISO-8859-1)
            updatedText.toByteArray(Charset.forName("ISO-8859-1"))
        } else {
            // GBK encoding (this may require a specific library in Kotlin/Java)
            try {
                updatedText.toByteArray(Charset.forName("GBK"))
            } catch (e: Exception) {
                throw IllegalArgumentException("GBK encoding is not supported or not available.", e)
            }
        }
    }


    fun isChinese(c: Char): Boolean {
        // Implement logic to check if character is Chinese
        return false // Example placeholder
    }

    fun getLexemes(text: String): Pair<List<String>, List<Boolean>> {
        // Implement logic to get lexemes from text
        return Pair(listOf(text), listOf(false)) // Example placeholder
    }



    fun text(
        text: String,
        styles: PosStyles = PosStyles(),
        linesAfter: Int = 0,
        containsChinese: Boolean = false,
        maxCharsPerLine: Int? = null
    ): MutableList<Byte> {
        val bytes = mutableListOf<Byte>()

        if (!containsChinese) {
            // Assuming _encode and _text are properly implemented elsewhere in your code
            bytes.addAll(
                _text(
                    encode(text, isKanji = containsChinese),
                    styles = styles,
                    isKanji = containsChinese,
                    maxCharsPerLine = maxCharsPerLine
                )
            )
            // Ensure at least one line break after the text
            bytes.addAll(emptyLines(linesAfter + 1))
        } else {
            // Assuming _mixedKanji is implemented similarly for the Chinese case
            bytes.addAll(_mixedKanji(text, styles = styles, linesAfter = linesAfter))
        }

        return bytes
    }


    private fun emptyLines(n: Int): List<Byte> {
        val bytes = mutableListOf<Byte>()
        if (n > 0) {
            // Create a list with `n` newline characters and convert to their byte values
            val newLines = "\n".repeat(n)  // Create a string with `n` newline characters
            bytes.addAll(newLines.toByteArray().map { it})
        }
        return bytes
    }

    fun _mixedKanji(
        text: String,
        styles: PosStyles = PosStyles(),
        linesAfter: Int = 0,
        maxCharsPerLine: Int? = null
    ): List<Byte> {
        val bytes = mutableListOf<Byte>()
        val (lexemes, isLexemeChinese) = getLexemes(text)

        // Print each lexeme using codetable OR kanji
        var colInd: Int? = 0
        for (i in lexemes.indices) {
            bytes += _text(
                encode(lexemes[i], isKanji = isLexemeChinese[i]),
                styles = styles,
                colInd = colInd,
                isKanji = isLexemeChinese[i],
                maxCharsPerLine = maxCharsPerLine
            )
            // Define the absolute position only once (we print one line only)
            colInd = null
        }

        bytes += emptyLines(linesAfter + 1)
        return bytes
    }


    fun _text(
        textBytes: ByteArray,
        styles: PosStyles = PosStyles(),
        colInd: Int? = 0,
        isKanji: Boolean = false,
        colWidth: Int = 12,
        maxCharsPerLine: Int? = null
    ): MutableList<Byte> {
        val bytes = mutableListOf<Byte>()

        // If colInd is not null, calculate the position and add the hex pair
        colInd?.let {
            val charWidth = _getCharWidth(styles, maxCharsPerLine = maxCharsPerLine)
            var fromPos = _colIndToPosition(colInd)

            // Align text within column width
            if (colWidth != 12) {
                val toPos = _colIndToPosition(colInd + colWidth) - spaceBetweenRows
                val textLen = textBytes.size * charWidth

                fromPos = when (styles.align) {
                    PosAlign.RIGHT -> toPos - textLen
                    PosAlign.CENTER -> fromPos + (toPos - fromPos) / 2 - textLen / 2
                    else -> fromPos
                }

                if (fromPos < 0) fromPos = 0.0
            }

            val hexStr = fromPos.roundToInt().toString(16).padStart(3, '0')
            val hexPair = hexStr.chunked(2).map { it.toInt(16).toByte() }
            // Position
            bytes += cPos.toByteArray().toList() + hexPair
        }

        // Add styles and text bytes
        bytes += setStyles(styles, isKanji).toList()
        bytes += textBytes.toList()

        return bytes
    }

    private fun setStyles(styles: PosStyles, isKanji: Boolean = false): List<Byte> {
        val bytes = mutableListOf<Byte>()

        // Handle alignment
        if (styles.align != _styles.align) {
            bytes += when (styles.align) {
                PosAlign.LEFT -> C_ALIGN_LEFT.toByteArray().toList()
                PosAlign.CENTER -> C_ALIGN_CENTER.toByteArray().toList()
                PosAlign.RIGHT -> C_ALIGN_RIGHT.toByteArray().toList()
            }
            _styles = _styles.copy(align = styles.align)
        }

        // Handle bold style
        if (styles.bold != _styles.bold) {
            bytes += if (styles.bold) cBoldOn.toByteArray().toList()
            else cBoldOff.toByteArray().toList()
            _styles = _styles.copy(bold = styles.bold)
        }

        // Handle 90-degree rotation
        if (styles.turn90 != _styles.turn90) {
            bytes += if (styles.turn90) cTurn90On.toByteArray().toList() else cTurn90Off.toByteArray().toList()
            _styles = _styles.copy(turn90 = styles.turn90)
        }

        // Handle reverse mode
        if (styles.reverse != _styles.reverse) {
            bytes += if (styles.reverse) cReverseOn.toByteArray().toList() else cReverseOff.toByteArray().toList()
            _styles = _styles.copy(reverse = styles.reverse)
        }

        // Handle underline
        if (styles.underline != _styles.underline) {
            bytes += if (styles.underline) cUnderline1dot.toByteArray().toList() else cUnderlineOff.toByteArray().toList()
            _styles = _styles.copy(underline = styles.underline)
        }

        // Set font
        if (styles.fontType != null && styles.fontType != _styles.fontType) {
            bytes += if (styles.fontType == PosFontType.FONT_B) cFontB.toByteArray().toList() else cFontA.toByteArray().toList()
            _styles = _styles.copy(fontType = styles.fontType)
        } else if (_font != null && _font != _styles.fontType) {
            bytes += if (_font == PosFontType.FONT_B) cFontB.toByteArray().toList() else cFontA.toByteArray().toList()
            _styles = _styles.copy(fontType = _font)
        }

        // Handle character size
        if (styles.height.value != _styles.height.value || styles.width.value != _styles.width.value) {
            val sizeBytes = mutableListOf<Byte>()
            sizeBytes += cSizeGSn.toByteArray().toList()
            sizeBytes += PosTextSize.decSize(styles.height, styles.width).toByte()
            bytes += sizeBytes
            _styles = _styles.copy(height = styles.height, width = styles.width)
        }

        // Set Kanji mode
        if (isKanji) {
            bytes += cKanjiOn.toByteArray().toList()
        } else {
            bytes += cKanjiOff.toByteArray().toList()
        }

        // Set local code table
        /*if (styles.codeTable != null) {
            val codeTableBytes = mutableListOf<Byte>()
            codeTableBytes += cCodeTable.toByteArray().toList()
            codeTableBytes += _profile.getCodePageId(styles.codeTable).toByte()
            bytes += codeTableBytes
            _styles = _styles.copy(align = styles.align, codeTable = styles.codeTable)
        } else if (_codeTable != null) {
            val codeTableBytes = mutableListOf<Byte>()
            codeTableBytes += cCodeTable.toByteArray().toList()
            codeTableBytes += _profile.getCodePageId(_codeTable).toByte()
            bytes += codeTableBytes
            _styles = _styles.copy(align = styles.align, codeTable = _codeTable)
        }*/

        return bytes
    }



}