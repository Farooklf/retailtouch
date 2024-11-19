package com.lfssolutions.retialtouch.utils.printer

import com.github.mustachejava.DefaultMustacheFactory
import java.io.PrintWriter
import java.io.StringReader
import java.io.StringWriter
import java.io.Writer


actual class TemplateRenderer actual constructor(){

    actual fun renderInvoiceTemplate(
        template: String,
        data: Map<String, Any?>
    ): String {
        // Perform the replacement in the template string
        val renderedTemplate = template
        // Create a Mustache factory instance
        val mf = DefaultMustacheFactory()
        val mustache = mf.compile(StringReader(renderedTemplate), "invoiceTemplate")
        // Use StringWriter to capture the rendered output
        val writer = StringWriter()
        mustache.execute(writer, data).flush()
        //val writer=mustache.execute(PrintWriter(System.out), data)
       //  writer.flush()
        println("Template: $writer")
        // Return the rendered template as a String
        return writer.toString()


        // Create a map to store placeholders and their values
        /*val replacements = mutableMapOf<String, String>()
        // Populate the map with values from the InvoiceData
        replacements["{{invoice.invoiceNo}}"] = data.invoiceNo
        replacements["{{invoice.invoiceDate}}"] = data.invoiceDate
        replacements["{{invoice.terms}}"] = data.terms
        replacements["{{invoice.customerName}}"] = data.customerName
        replacements["{{customer.address1}}"] = data.address1
        replacements["{{customer.address2}}"] = data.address2
        replacements["{{invoice.qty}}"] = data.qty.toString()
        replacements["{{invoice.invoiceSubTotal}}"] = data.invoiceSubTotal.toString()
        replacements["{{invoice.tax}}"] = data.tax.toString()
        replacements["{{invoice.netTotal}}"] = data.netTotal.toString()
        replacements["{{customer.balanceAmount}}"] = data.balanceAmount.toString()
        replacements["{{invoice.signature}}"] = data.signature
        replacements["{{invoice.qrUrl}}"] = data.qrUrl


        // Handle items (replacing items in the template)
        val itemsReplacement = data.items.joinToString("\n") { item ->
            "[L]${item.index}. ${item.productName}\n{6,6}[R] ${item.qty} X PCS ${item.price} | [R]${item.netTotal}"
        }

        // Replace the {{#items}} block in the template with the generated items string
        renderedTemplate = renderedTemplate.replace("{{#items}}", itemsReplacement).replace("{{/items}}", "")

        replacements.forEach { (placeholder, value) ->
            renderedTemplate = renderedTemplate.replace(placeholder, value)
        }

        // Return the rendered template
        return renderedTemplate*/
    }
}
