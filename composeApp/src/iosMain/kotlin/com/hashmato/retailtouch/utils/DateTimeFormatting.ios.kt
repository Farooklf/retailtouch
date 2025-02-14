package com.hashmato.retailtouch.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

actual class DateFormatter {

    actual fun getCurrentDateAndTimeInEpochMilliSeconds(): Long {
        return Clock.System.now().toEpochMilliseconds()
    }

    actual fun getHoursDifferenceFromEpochMilliseconds(
        savedTime: Long,
        currentTime: Long
    ): Long {
        val startInstant = Instant.fromEpochMilliseconds(savedTime)
        val currentInstant = Instant.fromEpochMilliseconds(currentTime)
        return currentInstant.minus(startInstant).inWholeHours
    }

}