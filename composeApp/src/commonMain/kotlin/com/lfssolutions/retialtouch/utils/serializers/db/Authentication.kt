package com.lfssolutions.retialtouch.utils.serializers.db

import com.lfssolutions.retialtouch.domain.model.login.LoginResponse
import com.lfssolutions.retialtouch.utils.JsonObj
import kotlinx.serialization.encodeToString

fun LoginResponse.toJson(): String = JsonObj.encodeToString(this)

fun String.toLogin(): LoginResponse = JsonObj.decodeFromString(this)