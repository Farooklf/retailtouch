package com.hashmato.retailtouch.utils



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


val posSettlementDefaultTemplate1= """
    [CA]SETTLEMENT\\n[C]---------------------------------------------------------------------------------------\\n[CA]{{settlement.date}}\\n[C]---------------------------------------------------------------------------------------\\nPay By    Actual    Entered    Difference\\n[C]---------------------------------------------------------------------------------------\\n{{#items}}\\n{{&paymentType}}    {{serverAmount}}    {{localAmount}}    {{diff}}\\n{{/items}}\\n[C]---------------------------------------------------------------------------------------\\n           {{settlement.itemTotal}}    {{settlement.localTotal}}    {{settlement.diff}}    \\n[C]---------------------------------------------------------------------------------------\\n[XXX]
"""

val posSettlementDefaultTemplate = """
[C]<font size='big'>SETTLEMENT</font>   
[[Line]]
[C]<font size='big'>{{date}}</font>  
[[Line]]
Pay By  Actual  Entered  Difference
[[Line]]
{posSettlementDetails}
[[Line]]   
[C]{{itemTotal}}    {{localTotal}}    {{diff}}  
[[Line]] 
[XXX]
   
<!-- posSettlementDetails Table -->
<ListItem>
{{paymentType}}    {{serverAmount}}    {{localAmount}}    {{diff}}
</ListItem>   
"""

val POSInvoiceDefaultTemplate = """
@@@http://tajmahal.rtlconnect.net/common/upload/Taj2.bmp

[L]<b>TAJ MAHAL FOOD PTE LTD</b>
[L]UEN NO- 201705269N
[L]8A ADMIRALTY ST
[L]#07-26, FOOD XCHANGE@ADMIRALITY
[L]Singapore 757437
[L]Customer Care- 8585 8584 / 8485 8585

[[Line]]
[C]<b>TAX INVOICE</b>
[[Line]]

[L]Invoice No: {{invoiceNo}}
[L]Date: {{invoiceDate}}
[[{4,8}:Customer|{{customerName}}]]
[[{4,8}:Address|{{address1}}]]
[[{4,8}:|{{address2}}]]
[[Line]]
[[{8,4}:Description|Amount]]
[[Line]]
{posInvoiceDetails}
[[Line]]
[[{4,8}:Qty|{{qty}}]]
[[Line]]
[[{8,4}:Sub Total|{{invoiceSubTotal}}]]
[[{8,4}:Item Discount|{{invoiceItemDiscount}}]]
[[{8,4}:Discount{{invoiceNetDiscountPer}}|{{invoiceNetDiscount}}]]
[[{8,4}:Gst|{{invoiceTax}}]]
[[{8,4}:Net Total|{{invoiceNetTotal}}]]
[[Line]]
{posPayments}
[[Line]]


[[{6,6}:Received By |Delivered By]]
[[{6,6}:[L] | [R]]]

@@{{&invoice.signature}}@@

[[{6,6}:-------------|--------------]]
###{{invoice.qrUrl}}

<!-- posPayments Table -->
<ListItem> 
[[{6,6}:[L]{{name}} |{{amount}}]]
</ListItem>

<!-- posInvoiceDetails Table -->
<ListItem>
[L]{{inventoryName}}
[[{6,6}:{{qty}} X PCS {{price}} {{itemDiscount}} |[R]{{netCost}}]]
</ListItem>
"""
/*
[[{6,6}:{{qty}} X PCS {{price}} |[R]{{netTotal}}]]
[[{8,4}:Outstanding Amt |{{invoiceOutstandingAmt}}]]*/
/*[[{8,4}:Promotion Discount |{{invoicePromotionDiscount}}]]*/

