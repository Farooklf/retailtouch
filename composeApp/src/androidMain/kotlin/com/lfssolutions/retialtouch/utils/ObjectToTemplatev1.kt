import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.dantsu.escposprinter.EscPosPrinterCommands
import com.dantsu.escposprinter.EscPosPrinterSize
import com.lfssolutions.retialtouch.utils.defaultTemplate2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ObjectToReceiptTemplateV1 {

    companion object {
        private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob()) // Background thread
        /**
         * Process HTML template by replacing placeholders with values from any data class
         */
        suspend fun processTemplate(
            template: String = defaultTemplate2,
            data: Any?,
            printerWidth: Float = 80f,
            decimalPoints: Int = 2
        ): String {
            var processedText = template
            if (data != null) {
                // Process single value placeholders
                data::class.java.declaredFields.forEach { prop ->
                    prop.isAccessible = true
                    val value = prop.get(data)
                     if (!value.isListType()) {
                        val placeholder = "{{${prop.name}}}"
                        val datePlaceHolder = "\\{\\{${prop.name}:(.+?)\\}\\}".toRegex()

                        //val formattedValue = formatValue(value, decimalPoints)

                        // Regex pattern to match the full row containing the placeholder
                        //val rowRegex = """\[\[.*?\|\s*\{\{${prop.name}\}\}\s*\]\](\n|\r\n)?""".toRegex()
                        // Debugging


                        if (datePlaceHolder.containsMatchIn(processedText)) {
                            processedText = applyDateFormat(
                                processedText,
                                datePlaceHolder,
                                value
                            )
                        }
                        /*else if (isZeroValue(value)) { // Check for zero value
                            // *** KEY CHANGE: Use prop.name, NOT the full field name ***
                            //val placeholder = "{{${prop.name}}}"  // Get the placeholder string
                            val rowRegex = """\[\[\s*\{\d+,\d+}:.*\|\s*${placeholder}\s*]](\r?\n)?""".toRegex() // More flexible whitespace //Use placeholder in regex

                            //val rowRegex = """\[\[\s*\{\d+,\d+}:.*?\|\s*\{\{$prop.name\}\}\s*]](\r?\n)?""".toRegex()
                            if (rowRegex.containsMatchIn(processedText)) {
                                println("Removing row for: ${prop.name}, Value: $value, Regex: $rowRegex")
                                processedText = processedText.replace(rowRegex, "")
                            } else {
                                // Handle cases where the placeholder might be alone in a row or structured differently
                                val simplePlaceholderRegex = """\[\[.*?\{\{$prop.name\}\}.*?\]\](\r?\n)?""".toRegex()
                                if (simplePlaceholderRegex.containsMatchIn(processedText)){
                                    println("Removing row for: ${prop.name}, Value: $value, Regex: $simplePlaceholderRegex")
                                    processedText = processedText.replace(simplePlaceholderRegex, "")
                                }
                                println("No match found for: ${prop.name}, Value: $value, Regex: $rowRegex or $simplePlaceholderRegex")
                                processedText = processedText.replace(placeholder,"") //Remove placeholder if not in row
                            }

                        }*/
                        /*else if (value == "0" || value == "0.0") {

                            val testString = """[[{4,8}:Item Discount|{{invoiceItemDiscount}}]]"""
                            //val testRegex = """\[\[\s*\{?\{[^}]*\}\}:[^|]*\|\s*\{\{invoiceItemDiscount\}\}\s*\}\]\](\r?\n)?""".toRegex()
                            val rowRegex ="""\[\[\s*\{\d+,\d+}:.*?\|\s*\{\{$value\}\}\s*]](\r?\n)?""".toRegex()
                            println("TestedRgx ${rowRegex.containsMatchIn(testString)}")  // Should print `true` if the match is successful

                            // Log to see if the regex is working
                            if (rowRegex.containsMatchIn(processedText)) {
                                println("Removing row for: ${prop.name}, Regex: $rowRegex")
                                processedText = processedText.replace(rowRegex, "")
                            } else {
                                println("No match found for: ${prop.name}, Regex: $rowRegex")
                            }
                        }*/
                        else {
                            // Normal replacement
                            //processedText = processedText.replace(placeholder, formattedValue)
                            processedText = processedText.replace(
                                placeholder,
                                formatValue(value, decimalPoints)
                            )
                        }
                    }
                    else {
                        val listItems = value as? List<*>
                        if (!listItems.isNullOrEmpty()) {
                            val itemTemplate = extractListItemTemplate(template, prop.name)
                            if (itemTemplate != null) {
                                val processedItems =
                                    processListItems(
                                        listItems,
                                        itemTemplate,
                                        template,
                                        decimalPoints
                                    )
                                processedText =
                                    processedText.replace("{${prop.name}}", processedItems)
                                processedText = processedText.replace(itemTemplate, "")

                            }
                        } else {
                            val itemTemplate = extractListItemTemplate(template, prop.name)
                            if (itemTemplate != null) {
                                processedText = processedText.replace("{${prop.name}}", "")
                                processedText = processedText.replace(itemTemplate, "")
                            }
                        }
                    }

                }
                Log.e("template", "before table processed text $processedText")

                val holdingZeroValuesRegex = Regex("\\[\\[\\{\\d+,\\d+\\}:[^\\]|]+\\|0\\.00\\]\\]")
                if(holdingZeroValuesRegex.containsMatchIn(processedText)){
                    //processedText = removeZeroValueLines(processedText)
                    processedText = processedText.replace(holdingZeroValuesRegex, "").replace("\n\n", "\n").trim() // Clean empty lines
                    //println("cleanedTemplate : $processedText")
                }


                //Apply image
                //processedText=extractAndReplaceImageUrl(processedText,printer)
                val imageRegex = """@@@(http[s]?://\S+)""".toRegex()
                if(imageRegex.containsMatchIn(processedText)){
                    val match = imageRegex.find(processedText) // Finds the first match
                    val matchResults = match?.groupValues?.get(1)
                    //println("ExtractUrl :$matchResults")
                    matchResults?.let {imageUrl ->
                        val imageBitmap = loadImageFromUrl(imageUrl) // Load image from URL
                        imageBitmap?.let { image->
                            val hexString = processUrlImage(image, printerWidth)
                            processedText=processedText.replace(
                                imageRegex,
                                hexString
                            )
                        }
                    }
                }

                //Apply weight to columns
                val tableWeightRegex = Regex("\\[\\[(.*?):(.*)\\]\\]")
                val matchResults = tableWeightRegex.findAll(processedText)
                matchResults.forEach {
                    println("Match found: ${it.value}") // Print the matched string
                    processedText = processedText.replace(
                        it.value,
                        processWeightTableTexts(it, printerWidth)
                    )
                }

                //Apply lines
                val lineRegex = Regex("\\[\\[Line\\]\\]")
                val lineMatchResults = lineRegex.findAll(processedText)
                lineMatchResults.forEach {
                    println("Match found: ${it.value}") // Print the matched string
                    processedText = processedText.replace(
                        it.value,
                        processLine(printerWidth)
                    )
                }
                processedText = cleanUpPlaceholders(processedText)
                Log.e("Netemplate", "After processedHtml $processedText")
            }

            return processedText.trim()
        }

        private fun removeZeroValueLines(template: String): String {
            return template
                .lineSequence() // Process line by line
                .filterNot { it.contains("|0.00]") } // Remove lines containing 0.00 values
                .joinToString("\n") // Join back the filtered lines
        }

        private fun isZeroValue(value: Any?): Boolean {
            return when (value) {
                is Int -> value == 0
                is Long -> value == 0L
                is Float -> value == 0.0f
                is Double -> value == 0.0
                is String -> value == "0" || value == "0.0"
                else -> false // Or handle other numeric types as needed
            }
        }

        /**
         * Process each item in a list using the item template, including nested lists
         */
        private fun processListItems(
            items: List<*>,
            itemTemplate: String,
            fullTemplate: String,
            decimalPoints: Int
        ): String {
            return items.mapNotNull { item ->
                item?.let { nonNullItem ->

                    var processedItem = itemTemplate
                    kotlin.runCatching {
                        // Process regular properties
                        nonNullItem::class.java.declaredFields.forEach { prop ->
                            kotlin.runCatching {
                                prop.isAccessible = true
                                val value = prop.get(nonNullItem)
                                Log.e("Netemplate", "processListItems ${prop.name}")

                                if (!value.isListType()) {
                                    val placeholder = "{{${prop.name}}}"
                                    processedItem =
                                        processedItem.replace(
                                            placeholder,
                                            formatValue(value, decimalPoints)
                                        )
                                } else {
                                    // Process nested list
                                    val nestedList = value as? List<*>
                                    if (!nestedList.isNullOrEmpty()) {
                                        val nestedTemplate =
                                            extractListItemTemplate(fullTemplate, prop.name)
                                        if (nestedTemplate != null) {
                                            val processedNestedItems = processListItems(
                                                nestedList,
                                                nestedTemplate,
                                                fullTemplate,
                                                decimalPoints
                                            )
                                            processedItem = processedItem.replace(
                                                "{${prop.name}}",
                                                processedNestedItems
                                            )
                                            processedItem =
                                                processedItem.replace(nestedTemplate, "")

                                        }
                                    } else {
                                        // Remove the placeholder if list is empty
                                        processedItem = processedItem.replace("{${prop.name}}", "")
                                        val nestedTemplate =
                                            extractListItemTemplate(fullTemplate, prop.name)
                                        if (nestedTemplate != null) {
                                            processedItem =
                                                processedItem.replace(nestedTemplate, "")
                                        }
                                    }
                                }
                            }.onFailure { e ->
                                println("Error processing property ${prop.name}: ${e.message}")
                            }
                        }
                    }.onFailure { e ->
                        println("Error processing item: ${e.message}")
                    }
                    processedItem
                }
            }.joinToString("\n")
        }

        /*
        * Load image bitmap and convert into hexadecimal
        * */
        private suspend fun loadImageFromUrl(imageUrlPrint: String): Bitmap? = withContext(Dispatchers.IO) {
            try {
                val url = URL(imageUrlPrint)
                BitmapFactory.decodeStream(url.openConnection().getInputStream())
            } catch (e: Exception) {
                Log.e("loadImageFromWebEx", e.toString())
                e.printStackTrace()
                null
            }
        }
        private fun processUrlImage(imageBitmap: Bitmap, printerWidthPx: Float): String {
            val hexString =
                bytesToHexadecimalString(bitmapToBytes(imageBitmap, true, printerWidthPx))
            val replaceCode = "[C]<img>$hexString</img>\n"
            //println("HexStringWithImage :- $replaceCode")
            return replaceCode
        }

        // convert Image bitmap to Base64
        private fun bitmapToBase64(bm: Bitmap): String? {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        }

        /**
         * Extract template section for list items
         */
        fun extractListItemTemplate(template: String, listName: String): String? {
            val startTag = "<ListItem>"
            val endTag = "</ListItem>"

            val sectionStart = template.indexOf("<!-- $listName Table -->")
            if (sectionStart == -1) return null

            val contentStart = template.indexOf(startTag, sectionStart)
            if (contentStart == -1) return null

            val contentEnd = template.indexOf(endTag, contentStart)
            if (contentEnd == -1) return null

            return template.substring(contentStart + startTag.length, contentEnd).trim()
        }

        /**
         * Formats a given value based on its type.
         *
         * This function formats values depending on their type. For `Double` values, it rounds them to the specified
         * number of decimal points. For other types, it simply converts them to strings.
         *
         * @param value The value to be formatted.
         * @param decimalPoints The number of decimal points to format numerical values (defaults to 2).
         *
         * @return The formatted string representation of the value.
         */
        private fun formatValue(value: Any?, decimalPoints: Int): String {
            return when (value) {
                null -> ""
                is Double -> String.format("%.${decimalPoints}f", value)
                else -> value.toString()
            }
        }

        fun checkValueIsDisplay(value: Any?): Boolean {
            return when (value) {
                null -> false
                is String -> value != "0.0" && value.isNotBlank()
                is Number -> value.toDouble() != 0.0
                else -> true
            }
        }

        /**
         * Check if a value is a List type
         */
        private fun Any?.isListType(): Boolean {
            return this is List<*>
        }


        /*CHECK IF A VALUE IS IMAGE */
        private fun hasImageUrl(template:String):Boolean{
            val imageRegex = "@@@(.*?)@@@".toRegex() // Regular expression for matching image URLs
            return imageRegex.containsMatchIn(template)
        }
        private fun calculateTotalWidth(
            paperWidthMm: Float,
            dpi: Int,
            characterWidthMm: Float
        ): Int {
            // Convert paper width to printable dots
            val paperWidthDots = (paperWidthMm / 25.4f) * dpi
            // Convert dots to characters based on character width in mm
            return (paperWidthDots / (characterWidthMm * dpi / 25.4f)).toInt()
        }

        private fun calculateColumnWidths(weights: IntArray, totalWidth: Int): IntArray {
            // Ensure the weights array is not empty
            if (weights.isEmpty() || totalWidth <= 0) {
                println("Invalid input: weights array is empty or totalWidth is non-positive")
                return intArrayOf()
            }

            // Calculate the total weight
            val totalWeight = weights.sum()

            // Ensure the totalWeight is not zero to avoid division by zero
            if (totalWeight == 0) {
                println("Total weight is zero, cannot proceed with column width calculation")
                return intArrayOf()
            }

            // Calculate the proportional widths
            return weights.map { weight ->
                // Proportional width based on the weight and total width
                val columnWidth = (weight.toDouble() / totalWeight * totalWidth).toInt()
                println("Weight: $weight, Column Width: $columnWidth")
                columnWidth
            }.toIntArray()
        }

        fun processWeightTableTexts(matchResult: MatchResult, printerWidth: Float): String {
            val beforeColon = matchResult.groupValues[1]
            val afterColon = matchResult.groupValues[2]

            val splitBeforeColon = beforeColon.removeSurrounding("{", "}").split(",")
                .map { it.trim().toInt() }
                .toIntArray()

            val splitAfterColon = afterColon.split("|")
            val totalWidth = calculateTotalWidth(printerWidth, 203, 2f) //previously taken 1.25f
            val columnWidths = calculateColumnWidths(splitBeforeColon, totalWidth)

            return printMultiLineTable(splitAfterColon, columnWidths)
        }

        private fun printMultiLineTable(
            data: List<String>,
            columnWidths: IntArray
        ): String {
            // Function to intelligently split text
            fun splitTextIntelligently(text: String, columnWidth: Int): List<String> {
                // If text is shorter than column width, return as is
                if (text.length <= columnWidth) return listOf(text)

                // Try to find a word break within the column width
                val words = text.split("\\s+".toRegex())
                val lines = mutableListOf<String>()
                var currentLine = ""

                for (word in words) {
                    // If adding the word would exceed column width, start a new line
                    if ((currentLine + " " + word).trim().length > columnWidth) {
                        // If current line is empty, force-break the word
                        if (currentLine.isEmpty()) {
                            lines.add(word.take(columnWidth))
                            currentLine = word.drop(columnWidth)
                        } else {
                            lines.add(currentLine.trim())
                            currentLine = word
                        }
                    } else {
                        currentLine = (currentLine + " " + word).trim()
                    }
                }

                // Add last line if not empty
                if (currentLine.isNotEmpty()) {
                    lines.add(currentLine)
                }

                return lines
            }

            // Split each column's text intelligently
            val columnLines = data.mapIndexed { index, cell ->
                splitTextIntelligently(cell, columnWidths[index])
            }

            // Calculate maximum lines needed
            val maxLines = columnLines.maxOfOrNull { it.size } ?: 1

            // Build the table as a string
            val tableBuilder = StringBuilder()

            // Print each line
            for (lineIndex in 0 until maxLines) {
                val lineContent = columnLines.mapIndexed { index, lines ->
                    val columnWidth = columnWidths[index]

                    // Get the text for this line of the column, or pad with empty string
                    val lineText = lines.getOrNull(lineIndex) ?: ""

                    // Pad or truncate to ensure consistent column width
                    lineText.padEnd(columnWidth)
                }.joinToString("")

                tableBuilder.append(lineContent).append("\n")
            }

            return tableBuilder.toString()
        }

        private fun applyDateFormat(
            processedHtml: String,
            datePlaceHolder: Regex,
            value: Any?
        ) = processedHtml.replace(datePlaceHolder) { matchResult ->
            val dateFormat = matchResult.groupValues[1]
            if (value != null && value is String) {
                try {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.000", Locale.US)
                    val dateValue = inputFormat.parse(value) ?: Date()
                    val formatter =
                        SimpleDateFormat(dateFormat, Locale.getDefault())
                    formatter.format(dateValue)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                    value.toString() // Fallback for invalid date format
                }
            } else {
                value?.toString() ?: ""
            }
        }

        /**
         * Cleans up unprocessed placeholders in the HTML template.
         *
         *
         * @param template The input template string to clean up.
         *
         * @return The cleaned-up template with unprocessed placeholders removed.
         */
        private fun cleanUpPlaceholders(template: String): String {
            var cleanedTemplate = template

            // Remove unprocessed {{placeholders}}
            val doubleCurlyBraceRegex = "\\{\\{[^}]*\\}\\}".toRegex()
            cleanedTemplate = cleanedTemplate.replace(doubleCurlyBraceRegex, "")
            // Remove unprocessed <ListItem> tags
            val listItemRegex = "<ListItem>[\\s\\S]*?</ListItem>".toRegex()
            cleanedTemplate = cleanedTemplate.replace(listItemRegex, "")
            // Remove unprocessed def tags
            val listItemdefRegex = "<!-- [\\s\\S]*?-->".toRegex()
            cleanedTemplate = cleanedTemplate.replace(listItemdefRegex, "")

            return cleanedTemplate.trimEmptyLines()
        }

        fun String.trimEmptyLines() = trim().replace("\n+".toRegex(), replacement = "\n")
        fun mmToPx(mmSize: Float): Int {
            return Math.round(mmSize * (203.toFloat()) / EscPosPrinterSize.INCH_TO_MM)
                .toInt()
        }

        private fun processLine(printerWidth: Float): String {
            return if (printerWidth == 80f) {
                "-".repeat(48)
            } else {
                "-".repeat(32)
            }
        }
        fun bitmapToBytes(bitmap: Bitmap, gradient: Boolean, printerWidthPx: Float): ByteArray {
            val printingWidthPx: Int = this.mmToPx(printerWidthPx)

            var bitmap = bitmap
            var isSizeEdit = false
            var bitmapWidth = bitmap.width
            var bitmapHeight = bitmap.height
            val maxWidth: Int = printingWidthPx
            val maxHeight = 256

            if (bitmapWidth > maxWidth) {
                bitmapHeight =
                    Math.round((bitmapHeight.toFloat()) * (maxWidth.toFloat()) / (bitmapWidth.toFloat()))
                bitmapWidth = maxWidth
                isSizeEdit = true
            }
            if (bitmapHeight > maxHeight) {
                bitmapWidth =
                    Math.round((bitmapWidth.toFloat()) * (maxHeight.toFloat()) / (bitmapHeight.toFloat()))
                bitmapHeight = maxHeight
                isSizeEdit = true
            }

            if (isSizeEdit) {
                bitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, true)
            }

            return EscPosPrinterCommands.bitmapToBytes(bitmap, gradient)
        }

        fun bytesToHexadecimalString(bytes: ByteArray): String {
            val imageHexString = java.lang.StringBuilder()
            for (aByte in bytes) {
                val hexString = Integer.toHexString(aByte.toInt() and 0xFF)
                if (hexString.length == 1) {
                    imageHexString.append("0")
                }
                imageHexString.append(hexString)
            }
            return imageHexString.toString()
        }
    }
}

/*if (isZeroValue(value)) {
                        val placeholder = "{{${prop.name}}"
                        val escapedPlaceholder = "{{${Regex.escape(prop.name)}}}"
                        println("prop.name: ${prop.name}")
                        println("placeholder: $placeholder")
                        println("Escaped placeholder: $escapedPlaceholder")
                        // *** IMPROVED REGEX - ANCHORED TO ROW START AND END ***
                        val rowRegex = """^\[\[.*?${escapedPlaceholder}.*?\]\]$""".toRegex()


                        println("Regex: $rowRegex")
                        println("Processed Text (before): $processedText")

                        if (rowRegex.containsMatchIn(processedText)) {
                            println("Match found!")
                            processedText = processedText.replace(rowRegex, "")
                            println("Processed Text (after): $processedText")
                        } else {
                            // If the specific row regex doesn't match, try removing just the placeholder
                            val placeholderOnlyRegex = """${escapedPlaceholder}""".toRegex()
                            if (placeholderOnlyRegex.containsMatchIn(processedText)) {
                                println("Placeholder Only Match Found!")
                                processedText = processedText.replace(placeholderOnlyRegex, "")
                            } else {
                                println("No match found!")
                            }

                        }
                    }*/