package com.hashmato.retailtouch.utils.serializers.db


import com.hashmato.retailtouch.domain.model.employee.POSEmployee
import com.hashmato.retailtouch.domain.model.employee.POSEmployeeRight
import com.hashmato.retailtouch.domain.model.invoiceSaleTransactions.SaleInvoiceItem
import com.hashmato.retailtouch.domain.model.posInvoices.PosSaleDetails
import com.hashmato.retailtouch.domain.model.posInvoices.PosSalePayment
import com.hashmato.retailtouch.domain.model.posInvoices.PendingSale
import com.hashmato.retailtouch.domain.model.products.POSProduct
import com.hashmato.retailtouch.domain.model.memberGroup.MemberGroupItem
import com.hashmato.retailtouch.domain.model.paymentType.PaymentMethod
import com.hashmato.retailtouch.domain.model.productBarCode.Barcode
import com.hashmato.retailtouch.domain.model.productLocations.ProductLocationItem
import com.hashmato.retailtouch.domain.model.products.CRSaleOnHold
import com.hashmato.retailtouch.domain.model.promotions.PriceBreakPromotionAttribute
import com.hashmato.retailtouch.domain.model.promotions.Promotion
import com.hashmato.retailtouch.domain.model.promotions.PromotionDetails
import com.hashmato.retailtouch.domain.model.invoiceSaleTransactions.SaleRecord
import com.hashmato.retailtouch.domain.model.location.Location
import com.hashmato.retailtouch.domain.model.members.Member
import com.hashmato.retailtouch.domain.model.sync.SyncItem
import com.hashmato.retailtouch.utils.JsonObj
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Location.toJson(): String = JsonObj.encodeToString(this)
fun String.toDefaultLocation(): Location = JsonObj.decodeFromString(this)

fun SaleInvoiceItem.toJson(): String = JsonObj.encodeToString(this)

fun SaleRecord.toJson(): String = JsonObj.encodeToString(this)

fun String.toSaleRecord(): SaleRecord = JsonObj.decodeFromString(this)
fun String.toSaleInvoiceItem(): SaleInvoiceItem = JsonObj.decodeFromString(this)

fun POSProduct.toJson(): String = JsonObj.encodeToString(this)

fun String.toProduct(): POSProduct = JsonObj.decodeFromString(this)

fun ProductLocationItem.toJson(): String = JsonObj.encodeToString(this)

fun String.toProductLocationItem(): ProductLocationItem = JsonObj.decodeFromString(this)

fun Barcode.toJson(): String = JsonObj.encodeToString(this)

fun String.toBarcode(): Barcode = JsonObj.decodeFromString(this)

fun Member.toJson(): String = JsonObj.encodeToString(this)

fun String.toMember(): Member = JsonObj.decodeFromString(this)

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