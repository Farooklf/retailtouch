package com.lfssolutions.retialtouch.utils



val defaultTemplate = """
@@@http://tajmahal.rtlconnect.net/common/upload/Taj2.bmp

[LB]TAJ MAHAL FOOD PTE LTD
[L]UEN NO- 201705269N
[L]8A ADMIRALTY ST
[L]#07-26, FOOD XCHANGE@ADMIRALITY
[L]Singapore 757437
[L]Customer Care- 8585 8584 / 8485 8585

[C]--------------------------------------------
[CB]TAX INVOICE
[C]--------------------------------------------

[L]Invoice No: {{invoice.invoiceNo}}
[L]Date: {{invoice.invoiceDate}}
[L]Terms: {{invoice.terms}}

{4,8}[L]Customer:|{{&invoice.customerName}}
{4,8}[L]Address:|{{customer.address1}}
{4,8}[L] |{{customer.address2}}

[C]--------------------------------------------
{8,4}[C]Description|[R]Amount
[C]--------------------------------------------

{{#items}}
[L]{{index}}. {{productName}}
{6,6}[R] {{qty}} X PCS {{price}} |[R]{{netTotal}}
{{/items}}

[C]--------------------------------------------
{4,8}[L] | Qty: {{invoice.qty}}          
[C]--------------------------------------------
{8,4}[L]Sub Total :|[R] {{invoice.invoiceSubTotal}}
{8,4}[L]Gst :  |[R]{{invoice.tax}}
{8,4}[L]Net Total :|[R] {{invoice.netTotal}}

[C]--------------------------------------------
{8,4}[L]Outstanding Amt:|[R] {{customer.balanceAmount}}
[C]--------------------------------------------

{6,6}Received By            |[R]Delivered By
{6,6}[L]        |[R] 

@#@{{&invoice.signature}}

{6,6}-------------         |[R]--------------
###{{invoice.qrUrl}}
"""