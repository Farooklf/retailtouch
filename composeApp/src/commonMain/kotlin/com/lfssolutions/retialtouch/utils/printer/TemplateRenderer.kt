package com.lfssolutions.retialtouch.utils.printer

// In the common module
expect class TemplateRenderer() {
    fun renderInvoiceTemplate(template: String, data: Map<String, Any?>): String
}