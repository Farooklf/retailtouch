package com.hashmato.retailtouch.utils.printer

// In the common module
actual class TemplateRenderer {
   actual suspend fun renderInvoiceTemplate(
        template: String,
        data: Map<String, Any?>
    ): String {
        return ""
    }
}