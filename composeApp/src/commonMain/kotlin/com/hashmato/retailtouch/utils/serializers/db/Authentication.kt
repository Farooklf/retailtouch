package com.hashmato.retailtouch.utils.serializers.db

import com.hashmato.retailtouch.domain.model.login.LoginResponse
import com.hashmato.retailtouch.utils.JsonObj
import kotlinx.serialization.encodeToString

fun LoginResponse.toJson(): String = JsonObj.encodeToString(this)

fun String.toLogin(): LoginResponse = JsonObj.decodeFromString(this)