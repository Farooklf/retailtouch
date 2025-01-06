package com.lfssolutions.retialtouch.utils.serializers.db


import com.lfssolutions.retialtouch.domain.model.employee.POSEmployee
import com.lfssolutions.retialtouch.domain.model.employee.POSEmployeeRight
import com.lfssolutions.retialtouch.domain.model.invoiceSaleTransactions.SaleInvoiceItem
import com.lfssolutions.retialtouch.domain.model.posInvoices.PosSaleDetails
import com.lfssolutions.retialtouch.domain.model.posInvoices.PosSalePayment
import com.lfssolutions.retialtouch.domain.model.posInvoices.PendingSale
import com.lfssolutions.retialtouch.domain.model.products.Product
import com.lfssolutions.retialtouch.domain.model.memberGroup.MemberGroupItem
import com.lfssolutions.retialtouch.domain.model.members.MemberItem
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentMethod
import com.lfssolutions.retialtouch.domain.model.productBarCode.Barcode
import com.lfssolutions.retialtouch.domain.model.productLocations.ProductLocationItem
import com.lfssolutions.retialtouch.domain.model.products.CRSaleOnHold
import com.lfssolutions.retialtouch.domain.model.promotions.PriceBreakPromotionAttribute
import com.lfssolutions.retialtouch.domain.model.promotions.Promotion
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionDetails
import com.lfssolutions.retialtouch.domain.model.invoiceSaleTransactions.SaleRecord
import com.lfssolutions.retialtouch.domain.model.location.Location
import com.lfssolutions.retialtouch.domain.model.sync.SyncItem
import com.lfssolutions.retialtouch.utils.JsonObj
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Location.toJson(): String = JsonObj.encodeToString(this)
fun String.toDefaultLocation(): Location = JsonObj.decodeFromString(this)

fun SaleInvoiceItem.toJson(): String = JsonObj.encodeToString(this)

fun SaleRecord.toJson(): String = JsonObj.encodeToString(this)

fun String.toSaleRecord(): SaleRecord = JsonObj.decodeFromString(this)
fun String.toSaleInvoiceItem(): SaleInvoiceItem = JsonObj.decodeFromString(this)

fun Product.toJson(): String = JsonObj.encodeToString(this)

fun String.toProduct(): Product = JsonObj.decodeFromString(this)

fun ProductLocationItem.toJson(): String = JsonObj.encodeToString(this)

fun String.toProductLocationItem(): ProductLocationItem = JsonObj.decodeFromString(this)

fun Barcode.toJson(): String = JsonObj.encodeToString(this)

fun String.toBarcode(): Barcode = JsonObj.decodeFromString(this)

fun MemberItem.toJson(): String = JsonObj.encodeToString(this)

fun String.toMemberItem(): MemberItem = JsonObj.decodeFromString(this)

fun MemberGroupItem.toJson(): String = JsonObj.encodeToString(this)

fun String.toMemberGroupItem(): MemberGroupItem = JsonObj.decodeFromString(this)

fun PaymentMethod.toJson(): String = JsonObj.encodeToString(this)

fun String.toPaymentTypeItem(): PaymentMethod = JsonObj.decodeFromString(this)

fun Promotion.toJson(): String = JsonObj.encodeToString(this)

fun String.toPromotion(): Promotion = JsonObj.decodeFromString(this)

fun PromotionDetails.toJson(): String = JsonObj.encodeToString(this)

fun String.toPromotionDetails(): PromotionDetails = JsonObj.decodeFromString(this)

fun SyncItem.toJson(): String = JsonObj.encodeToString(this)

fun String.toSyncItem(): SyncItem = JsonObj.decodeFromString(this)

fun POSEmployee.toJson(): String = JsonObj.encodeToString(this)

fun String.toPOSEmployee(): POSEmployee = JsonObj.decodeFromString(this)

fun POSEmployeeRight.toJson(): String = JsonObj.encodeToString(this)

fun String.toPOSEmployeeRight(): POSEmployeeRight = JsonObj.decodeFromString(this)

fun PendingSale.toJson(): String = JsonObj.encodeToString(this)

fun String.toPosInvoicePendingSaleRecord(): PendingSale = JsonObj.decodeFromString(this)

fun PosSalePayment.toJson(): String = JsonObj.encodeToString(this)

fun String.toPosPaymentConfigRecord(): PosSalePayment = JsonObj.decodeFromString(this)

fun PosSaleDetails.toJson(): String = JsonObj.encodeToString(this)

fun String.toPosInvoiceDetailRecord(): PosSaleDetails = JsonObj.decodeFromString(this)

fun CRSaleOnHold.toJson(): String = JsonObj.encodeToString(this)

fun String.toHoldSaleRecord(): CRSaleOnHold = JsonObj.decodeFromString(this)

fun convertToJsonString(
    attributes: List<PriceBreakPromotionAttribute>?
): String? {
    return attributes?.let {
        Json.encodeToString(it)
    }
}

fun parsePriceBreakPromotionAttributes(jsonString: String?): List<PriceBreakPromotionAttribute>? {
    return jsonString?.let {Json.decodeFromString(it)}
}