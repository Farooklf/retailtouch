package com.lfssolutions.retialtouch.utils.printer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.github.mustachejava.DefaultMustacheFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
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
        // Create a Mustache factory instance
        val mf = DefaultMustacheFactory()
        val mustache = mf.compile(StringReader(preprocessedTemplate), "invoiceTemplate")
        // Use StringWriter to capture the rendered output
        val writer = StringWriter()
        withContext(Dispatchers.IO) {
            mustache.execute(writer, data).flush()
        }
        //val writer=mustache.execute(PrintWriter(System.out), data)
        //  writer.flush()
        //println("Template: $writer")
        // Return the rendered template as a String
        return writer.toString()
    }


    private fun extractImageUrlFromTemplate(template: String): String? {
        val regex = Regex("@@@(http[s]?://[\\w\\-\\.]+(?:[\\w\\-\\/]*\\.[\\w]{2,})?)")
        val matchResult = regex.find(template)
        return matchResult?.groupValues?.get(1)  // Extract the matched URL from the template
    }

    private fun preprocessImageUrl(template: String): String {
        // Replace lines starting with "@@@" with <img> tag
        return template.lines().joinToString("\n") { line ->
            if (line.startsWith("@@@") && line.length > 3) {
                 line.substring(3)
                /*"<img src=\"$imageUrl\" alt=\"Image\" />"*/ // Example replacement
            } else {
                line
            }
        }
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

}
