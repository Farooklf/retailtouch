package com.lfssolutions.retialtouch.utils.printer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.github.mustachejava.DefaultMustacheFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import java.io.StringReader
import java.io.StringWriter
import java.net.URL


actual class TemplateRenderer actual constructor(){


    actual suspend fun renderInvoiceTemplate(
        template: String,
        data: Map<String, Any?>
    ): String {
        // Preprocess the template to handle image URLs
        val preprocessedTemplate = renderTemplateWithImage(template)

        // Apply the formatting (e.g., [L], [B], [U])
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
         println("Template: $writer")
        // Return the rendered template as a String
        return writer.toString()
    }


    private fun extractImageUrlFromTemplate(template: String): String? {
        val regex = Regex("@@@(http[s]?://[\\w\\-\\.]+(?:[\\w\\-\\/]*\\.[\\w]{2,})?)")
        val matchResult = regex.find(template)
        return matchResult?.groupValues?.get(1)  // Extract the matched URL from the template
    }


    // Function to extract the image URL from the template and replace it with the <img> tag
    private suspend fun renderTemplateWithImage(template: String): String {

        // Regex to match the image URL that starts with "@@@"
        val imageUrlRegex = """@@@(http[^\s]+)""".toRegex()
        val matchResult = imageUrlRegex.find(template)
        val imageUrl = matchResult?.groupValues?.get(1)
        println("imageUrl: $imageUrl")

        imageUrl?.let {
            val imageBitmap = loadImageFromWeb(it) // Load image from URL
            val hexString = bitmapToHexadecimalString(imageBitmap ?: return template)

            // Replace the image URL with the <img> tag containing the hexadecimal string
            val updatedTemplate = template.replace(imageUrlRegex, "[C]<img>$hexString</img>\n")
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
                println("rowContent:$rowContent")
                processedLines.add(processTableRow(rowSpecs, rowContent))
            } else if (line.isNotEmpty() && line.startsWith("[")) {
                val formatter = line.substring(1, line.indexOf(']'))
                val text = line.substring(line.indexOf(']') + 1)

                // Apply styles based on the formatter (L, C, B, U)
                val style = parseFormatterLine(formatter)
                println("style:$style")
                processedLines.add(applyStylesToText(style, text))
            } else {
                processedLines.add(line)
            }
        }

        formattedTemplate = processedLines.joinToString("\n")
        return formattedTemplate
    }

    private fun parseFormatterLine(formatter: String): PosStyles {
        var bold = false
        var underline = false
        var reverse = false
        var alignment = TextAlign.Start
        var fontSize = 14.sp

        for (ctl in formatter) {
            when (ctl) {
                'L', 'l' -> alignment = TextAlign.Left
                'C', 'c' -> alignment = TextAlign.Center
                'R', 'r' -> alignment = TextAlign.Right
                'B', 'b' -> bold = true
                'U', 'u' -> underline = true
                'I', 'i' -> reverse = true
                'a' ->  fontSize =14.sp
                'A' ->  fontSize =18.sp
            }
        }

        return PosStyles(
            bold = bold,
            alignment = alignment,
            underline = underline,
            reverse = reverse,
            fontSize = fontSize,
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
        return when (style.alignment) {
            TextAlign.Start -> "[L]$styledText"
            TextAlign.Center -> "[C]$styledText" //"<div style=\"text-align:center;\">$styledText</div>"
            TextAlign.Right -> "[R]$styledText" //"<div style=\"text-align:right;\">$styledText</div>"
            else -> {
                "[L]$styledText"
            }
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

        val rowBuilder = StringBuilder()
        columns.forEachIndexed { index, column ->
            val spec = rowSpecs[index]
            var columnText = column.trim()

            // Apply formatting if the column starts with a formatter
            if (columnText.startsWith("[")) {
                val formatter = columnText.substring(1, columnText.indexOf(']'))
                val text = columnText.substring(columnText.indexOf(']') + 1)
                val style = parseFormatterLine(formatter)
                columnText = applyStylesToText(style, text)
            }

            // Add column with spec width
            rowBuilder.append("[W$spec]$columnText")
        }

        return rowBuilder.toString()
    }


}

data class PosStyles(
    val bold: Boolean = false,
    val reverse: Boolean = false,
    val underline: Boolean = false,
    val alignment: TextAlign = TextAlign.Start,
    val fontSize: TextUnit = 14.sp
)

