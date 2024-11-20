package com.lfssolutions.retialtouch.utils.printer


actual class TemplateRenderer {
    actual suspend fun renderInvoiceTemplate(
        template: String,
        data: Map<String, Any?>
    ): String {
        return ""
    }
}