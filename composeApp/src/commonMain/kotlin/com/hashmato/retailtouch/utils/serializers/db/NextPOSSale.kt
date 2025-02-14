package com.hashmato.retailtouch.utils.serializers.db

import com.hashmato.retailtouch.domain.model.nextPOSSaleInvoiceNo.NextPOSSaleInvoiceNoResult
import com.hashmato.retailtouch.utils.JsonObj
import kotlinx.serialization.encodeToString

fun NextPOSSaleInvoiceNoResult.toJson(): String = JsonObj.encodeToString(this)

fun String.toNextPosSaleItem(): NextPOSSaleInvoiceNoResult = JsonObj.decodeFromString(this)