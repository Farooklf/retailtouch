package com.lfssolutions.retialtouch.utils.serializers.db


import com.lfssolutions.retialtouch.domain.model.employee.EmployeeDao
import com.lfssolutions.retialtouch.domain.model.posInvoices.PosInvoiceDetailRecord
import com.lfssolutions.retialtouch.domain.model.posInvoices.PosConfiguredPaymentRecord
import com.lfssolutions.retialtouch.domain.model.posInvoices.PosInvoicePendingSaleRecord
import com.lfssolutions.retialtouch.domain.model.products.Product
import com.lfssolutions.retialtouch.domain.model.memberGroup.MemberGroupItem
import com.lfssolutions.retialtouch.domain.model.members.MemberItem
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentMethod
import com.lfssolutions.retialtouch.domain.model.sales.POSInvoiceItem
import com.lfssolutions.retialtouch.domain.model.productBarCode.Barcode
import com.lfssolutions.retialtouch.domain.model.productLocations.ProductLocationItem
import com.lfssolutions.retialtouch.domain.model.products.CRSaleOnHold
import com.lfssolutions.retialtouch.domain.model.promotions.PriceBreakPromotionAttribute
import com.lfssolutions.retialtouch.domain.model.promotions.Promotion
import com.lfssolutions.retialtouch.domain.model.promotions.PromotionDetails
import com.lfssolutions.retialtouch.domain.model.sales.SaleRecord
import com.lfssolutions.retialtouch.domain.model.sync.SyncItem
import com.lfssolutions.retialtouch.utils.JsonObj
import comlfssolutionsretialtouch.HoldSaleRecord
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun SaleRecord.toJson(): String = JsonObj.encodeToString(this)

fun String.toSaleRecord(): SaleRecord = JsonObj.decodeFromString(this)

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

fun EmployeeDao.toJson(): String = JsonObj.encodeToString(this)

fun String.toEmployeeDao(): EmployeeDao = JsonObj.decodeFromString(this)

fun PosInvoicePendingSaleRecord.toJson(): String = JsonObj.encodeToString(this)

fun String.toPosInvoicePendingSaleRecord(): PosInvoicePendingSaleRecord = JsonObj.decodeFromString(this)

fun PosConfiguredPaymentRecord.toJson(): String = JsonObj.encodeToString(this)

fun String.toPosPaymentConfigRecord(): PosConfiguredPaymentRecord = JsonObj.decodeFromString(this)

fun PosInvoiceDetailRecord.toJson(): String = JsonObj.encodeToString(this)

fun String.toPosInvoiceDetailRecord(): PosInvoiceDetailRecord = JsonObj.decodeFromString(this)

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