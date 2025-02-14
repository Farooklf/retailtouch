package com.hashmato.retailtouch.utils.serializers

import io.kamel.core.utils.File
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun File.toJson(): String = Json.encodeToString(this)

private val json = Json { ignoreUnknownKeys = true }

fun String.toImageFiles(): List<File> =
    if (this.isBlank()) emptyList() else json.decodeFromString(this)