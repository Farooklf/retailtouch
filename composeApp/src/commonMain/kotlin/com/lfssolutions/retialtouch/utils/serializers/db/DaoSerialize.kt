package com.lfssolutions.retialtouch.utils.serializers.db


import com.lfssolutions.retialtouch.domain.model.memberGroup.MemberGroupItem
import com.lfssolutions.retialtouch.domain.model.members.MemberItem
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentTypeItem
import com.lfssolutions.retialtouch.domain.model.posInvoice.POSInvoiceItem
import com.lfssolutions.retialtouch.domain.model.productLocations.ProductLocationItem
import com.lfssolutions.retialtouch.domain.model.productWithTax.ProductTaxItem
import com.lfssolutions.retialtouch.domain.model.sync.SyncItem
import com.lfssolutions.retialtouch.utils.JsonObj
import kotlinx.serialization.encodeToString

fun POSInvoiceItem.toJson(): String = JsonObj.encodeToString(this)

fun String.toPosInvoiceItem(): POSInvoiceItem = JsonObj.decodeFromString(this)

fun ProductTaxItem.toJson(): String = JsonObj.encodeToString(this)

fun String.toProductTaxItem(): ProductTaxItem = JsonObj.decodeFromString(this)


fun ProductLocationItem.toJson(): String = JsonObj.encodeToString(this)

fun String.toProductLocationItem(): ProductLocationItem = JsonObj.decodeFromString(this)

fun MemberItem.toJson(): String = JsonObj.encodeToString(this)

fun String.toMemberItem(): MemberItem = JsonObj.decodeFromString(this)

fun MemberGroupItem.toJson(): String = JsonObj.encodeToString(this)

fun String.toMemberGroupItem(): MemberGroupItem = JsonObj.decodeFromString(this)

fun PaymentTypeItem.toJson(): String = JsonObj.encodeToString(this)

fun String.toPaymentTypeItem(): PaymentTypeItem = JsonObj.decodeFromString(this)

fun SyncItem.toJson(): String = JsonObj.encodeToString(this)

fun String.toSyncItem(): SyncItem = JsonObj.decodeFromString(this)