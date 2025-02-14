package com.hashmato.retailtouch.utils.printer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.github.mustachejava.DefaultMustacheFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.StringReader
import java.io.StringWriter
import java.net.URL
import android.util.Base64



actual class TemplateRenderer actual constructor(){


    actual suspend fun renderInvoiceTemplate(
        template: String,
        data: Map<String, Any?>
    ): String {
        // Preprocess the template to handle image URLs
        val preprocessedTemplate = renderTemplateWithImage(template)

        // Apply the formatting (e.g., [L], [B], [U])
        //val formattedTemplate = applyFormatted(preprocessedTemplate)
        val formattedTemplate = applyFormatted(preprocessedTemplate)

        // Create a Mustache factory instance
        val mf = DefaultMustacheFactory()
        val mustache = mf.compile(StringReader(formattedTemplate), "invoiceTemplate")
        // Use StringWriter to capture the rendered output
        val writer = StringWriter()
        withContext(Dispatchers.IO) {
            mustache.execute(writer, data).flush()
        }
        /*val writer = mustache.execute(PrintWriter(System.out), data)
        withContext(Dispatchers.IO) {
            writer.flush()
        }*/
         //println("Template: $writer")
        // Return the rendered template as a String
        return writer.toString()
    }


    private fun extractImageUrlFromTemplate(template: String): String? {
        val regex = Regex("@@@(http[s]?://[\\w\\-\\.]+(?:[\\w\\-\\/]*\\.[\\w]{2,})?)")
        val matchResult = regex.find(template)
        return matchResult?.groupValues?.get(1)  // Extract the matched URL from the template
    }


    suspend fun processTemplateForImg(template: String): String {
        val imageUrlRegex = """@@@(http\S+)""".toRegex()

        // Step 1: Find all matches
        val matches = imageUrlRegex.findAll(template).toList()

        // Step 2: Process each URL asynchronously
        val replacements = matches.map { match ->
            val imageUrl = match.groups[1]?.value
            val replacement = if (imageUrl != null) {
                try {
                    val imageBitmap = loadImageFromWeb(imageUrl) // Suspending function
                    if (imageBitmap != null) {
                        val hexString = bitmapToHexadecimalString(imageBitmap)
                        "[C]<img>$hexString</img>\n"
                    } else {
                        match.value // Retain original if loading fails
                    }
                } catch (e: Exception) {
                    println("Error processing image URL: $imageUrl - ${e.message}")
                    match.value // Retain original on error
                }
            } else {
                match.value // Retain original if URL extraction fails
            }
            match.range to replacement
        }

        // Step 3: Replace in the template
        val resultBuilder = StringBuilder(template)
        replacements.asReversed().forEach { (range, replacement) ->
            resultBuilder.replace(range.first, range.last + 1, replacement)
        }

        println("UpdatedTemplate :-$resultBuilder")
        return resultBuilder.toString()
    }


    // Function to extract the image URL from the template and replace it with the <img> tag
    private suspend fun renderTemplateWithImage(template: String): String {

        // Regex to match the image URL that starts with "@@@"
        val imageUrlRegex = """@@@(http[^\s]+)""".toRegex()
        val matchResult = imageUrlRegex.find(template)
        val imageUrl = matchResult?.groupValues?.get(1)
         println("imageUrl: $imageUrl")
         println("matchResult: $matchResult")

        imageUrl?.let {
             val imageBitmap = loadImageFromWeb(it) // Load image from URL
             val hexString = bitmapToBase64(imageBitmap ?: return template)
             println("hexString: $hexString")
            val replaceCode= "[C]<img>$hexString</img>\n"
            println("HexStringWith image :- $replaceCode")
            // Replace the image URL with the <img> tag containing the hexadecimal string
            val updatedTemplate = template.replaceFirst(imageUrlRegex, replaceCode)
            println("updatedTemplate $updatedTemplate")
            return updatedTemplate
        }

        return template // If no image URL is found, return the original template
    }


    private suspend fun loadImageFromWeb(imageUrlPrint: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val url = URL(imageUrlPrint)
            BitmapFactory.decodeStream(url.openConnection().getInputStream())
        } catch (e: Exception) {
            Log.e("loadImageFromWebEx", e.toString())
            e.printStackTrace()
            null
        }
    }

    // convert Image bitmap to Base64
    private fun bitmapToBase64(bm: Bitmap): String? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray,Base64.DEFAULT)
    }

    private fun bitmapToHexadecimalString(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        // Convert the byte array to hexadecimal string
        val stringBuilder = StringBuilder()
        for (byte in byteArray) {
            stringBuilder.append(String.format("%02X", byte))
        }
        return stringBuilder.toString()
    }

    private fun isLineStartsWithFormatterDescriptor(line: String): Boolean {
        return line.length > 3 && line[0] == '[' && line.contains(']')
    }

    private fun applyFormatted(template: String): String {

        var formattedTemplate = template

        // Extract formatting codes and apply styles
        val lines = formattedTemplate.split("\n")
        val processedLines = mutableListOf<String>()

        for (line in lines) {
            if (line.isEmpty()) {
                processedLines.add(line)
                continue
            }
            val rowSpecs = isTableRow(line)
            if (rowSpecs != null) {
                val rowContent = line.substring(line.indexOf('}') + 1)
                //println("rowContent:$rowContent")
                processedLines.add(processTableRow(rowSpecs, rowContent))
            } else if (line.isNotEmpty() && line.startsWith("[")) {
                val formatter = line.substring(1, line.indexOf(']'))
                val text = line.substring(line.indexOf(']') + 1)

                // Apply styles based on the formatter (L, C, B, U)
                val style = computeLineStyles(formatter)
                //println("style:$style")
                processedLines.add(applyStylesToText(style, text))
            } else {
                processedLines.add(line)
            }
        }

        formattedTemplate = processedLines.joinToString("\n")
        return formattedTemplate
    }

    private fun formatTemplate(template: String) : String {
        val generator=Generator(PaperSize.Mm80)
        val formattedLines = mutableListOf<String>()
       // Extract formatting codes and apply styles
        val linesArray = template.split("\n")
        for (line in linesArray){
            // Process each line and generate formatted content
            processLineAsString(cpl = 54,generator=generator, line=line, formattedLines = formattedLines)
        }

        // Combine all lines into one final formatted string
        return formattedLines.joinToString("\n")
    }

    private fun processLineAsString(
        cpl: Int,
        formattedLines: MutableList<String>,
        generator: Generator,
        line: String,
    ){
        val rowSpecs = isTableRow(line)

        if(rowSpecs != null){
            val rowFormatted = processTableRow(cpl = cpl,generator=generator, rowSpecs=rowSpecs,
                rowContent = line.substring(line.indexOf('}') + 1))
            formattedLines.add(rowFormatted)
        }else if (isLineStartsWithFormatterDescriptor(line)) {
            val formatter = line.substring(1, line.indexOf(']'))
            val text = line.substring(line.indexOf(']') + 1)
            // Apply styles based on the formatter (L, C, B, U)
            val style = computeLineStyles(formatter)
            //println("style:$style")
            formattedLines.add(applyStylesToText(style, text))
            // applyFormatted(cpl, generator, buffer, formatter, text)
        }else{
            formattedLines.add(line)
        }
    }

    private fun computeLineStyles(formatter: String): PosStyles {
        var bold = false
        var underline = false
        var reverse = false
        var alignment = PosAlign.LEFT
        var textSize = PosTextSize.size1

        for (ctl in formatter) {
            when (ctl) {
                'L', 'l' -> alignment = PosAlign.LEFT
                'C', 'c' -> alignment = PosAlign.CENTER
                'R', 'r' -> alignment = PosAlign.RIGHT
                'B', 'b' -> bold = true
                'U', 'u' -> underline = true
                'I', 'i' -> reverse = true
                'a' ->  textSize =PosTextSize.size1
                'A' ->  textSize =PosTextSize.size2
            }
        }

        return PosStyles(
            bold = bold,
            align = alignment,
            underline = underline,
            reverse = reverse,
            width = textSize,
            height = textSize
        )
    }

    private fun applyStylesToText(style: PosStyles, text: String): String {
        var styledText = text

        if (style.bold) {
            styledText = "<b>$styledText</b>"
        }
        if (style.underline) {
            styledText = "<u>$styledText</u>"
        }

        if (style.reverse) {
            styledText = "<i>$styledText</i>"
        }

        // Align text based on style.align
        return when (style.align) {
            PosAlign.LEFT-> "[L]$styledText"
            PosAlign.CENTER -> "[C]$styledText"
            PosAlign.RIGHT -> "[R]$styledText"
        }
    }

    private fun parseTableRowSpec(line: String): List<Int>? {
       // val regex = Regex("""\{(\d+(,\d+)*)}""") // Matches {1,2,3,...}
        val regex = Regex("\\{(\\d+(,\\d+)*)}")
        val matchResult = regex.find(line)
        if (matchResult != null) {
            val specs = matchResult.groupValues[1].split(",").mapNotNull { it.toIntOrNull() }
            return if (specs.sum() == 12 && specs.size > 1) specs else null
        }
        return null
    }

    private fun isTableRow(line: String): List<Int>? {
        // Check if the line starts with '{' and ends with '}'
        if (line.startsWith("{") && line.contains("}")) {
            val specsPart = line.substring(1, line.indexOf('}')) // Extract the part inside the braces
            val specs = specsPart.split(",") // Split by comma
            val intSpecs = mutableListOf<Int>()

            // Try parsing each spec to an integer
            var specsSum = 0
            for (spec in specs) {
                val parsedValue = spec.toIntOrNull()
                if (parsedValue != null) {
                    intSpecs.add(parsedValue)
                    specsSum += parsedValue
                } else {
                    return null // Return null if any value is not an integer
                }
            }

            // Check if the sum of specs equals 12 and there are more than one column
            return if (specsSum == 12 && intSpecs.size > 1) intSpecs else null
        }

        return null // Return null if the format is incorrect
    }

    private fun processTableRow(
        rowSpecs: List<Int>,
        rowContent: String
    ): String {
        val columns = rowContent.split("|")
        if (columns.size != rowSpecs.size) {
            return "!!! Error: columns != table specs !!!"
        }
         var styles = PosStyles()
        val rowBuilder = StringBuilder()
        columns.forEachIndexed { index, column ->
            val width = rowSpecs[index]
            var columnText = column.trim()

            // Apply formatting if the column starts with a formatter
            if (columnText.startsWith("[")) {
                val formatter = columnText.substring(1, columnText.indexOf(']'))
                val text = columnText.substring(columnText.indexOf(']') + 1)
                styles = computeLineStyles(formatter)
                columnText = applyStylesToText(styles, text)
            }

            // Determine the padding based on the alignment
            /*columnText = when (styles.align) {
                PosAlign.LEFT -> columnText.padEnd(width) // Left alignment: pad right
                PosAlign.RIGHT -> columnText.padStart(width) // Right alignment: pad left
                PosAlign.CENTER -> {
                    // Center alignment: pad both sides
                    val padLeft = (width - columnText.length) / 2
                    val padRight = width - columnText.length - padLeft
                    " ".repeat(padLeft) + columnText + " ".repeat(padRight)
                }
                else -> columnText.padEnd(width) // Default to left alignment if no align tag
            }*/

            // Add the formatted column to the row builder
            rowBuilder.append(columnText)
        }
        //println("columnText $rowBuilder")
        return rowBuilder.toString()
    }

    private fun getAlignmentType(columnText: String): String {
        // Extract the alignment type from the columnText (if it starts with [L], [R], [C])
        return when {
            columnText.startsWith("[L]") -> "L"
            columnText.startsWith("[R]") -> "R"
            columnText.startsWith("[C]") -> "C"
            else -> "L" // Default alignment is left
        }
    }

    private fun processTableRow(
        cpl: Int,
        generator: Generator,
        rowSpecs: List<Int>,
        rowContent: String
    ): String {
        val columns = rowContent.split("|")

//        if (columns.size != rowSpecs.size) {
//            //buffer.addAll(generator.text("!!! Error: columns != table specs !!!"))
//            return
//        }

        val row = mutableListOf<PosColumn>()

        for ((textIndex, spec) in rowSpecs.withIndex()) {
            var columnText = columns[textIndex]
            var columnStyles = PosStyles()

            if (isLineStartsWithFormatterDescriptor(columnText)) {
                columnStyles = computeLineStyles(
                    columnText.substring(1, columnText.indexOf(']'))
                )
                columnText = columnText.substring(columnText.indexOf(']') + 1)
            }

            row.add(PosColumn(text = columnText, width = spec, styles = columnStyles))
        }

        val formattedRow = generator.calculateRow(row)
        println("formattedRow | $formattedRow")
        return formattedRow.toString()
    }


    fun processRow(
        columnText : String,
        width:Int,
        style:PosStyles
    ){
        //processRow(columnText=columnText, width = spec, style = columnStyles)

    }

}


