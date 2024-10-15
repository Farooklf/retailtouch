package com.lfssolutions.retialtouch.utils.serializers.db

import com.lfssolutions.retialtouch.domain.model.nextPOSSaleInvoiceNo.NextPOSSaleInvoiceNoResult
import com.lfssolutions.retialtouch.utils.JsonObj
import kotlinx.serialization.encodeToString

fun NextPOSSaleInvoiceNoResult.toJson(): String = JsonObj.encodeToString(this)

fun String.toNextPosSaleItem(): NextPOSSaleInvoiceNoResult = JsonObj.decodeFromString(this)