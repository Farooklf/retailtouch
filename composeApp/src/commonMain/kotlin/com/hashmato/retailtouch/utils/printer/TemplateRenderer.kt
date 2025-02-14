package com.hashmato.retailtouch.utils.printer

// In the common module
expect class TemplateRenderer() {
    suspend fun renderInvoiceTemplate(template: String, data: Map<String, Any?>): String
}