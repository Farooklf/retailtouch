package com.lfssolutions.retialtouch.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter


actual class DateFormatter {


    actual fun getCurrentDateAndTimeInEpochMilliSeconds(): Long {
        return System.currentTimeMillis()
    }

    actual fun getHoursDifferenceFromEpochMilliseconds(
        savedTime: Long,
        currentTime: Long
    ): Long {
        return (currentTime - savedTime) / (1000 * 60 * 60) // Convert milliseconds to hours
    }

    actual fun formatDateWithTimeForApi(localDateTime: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        return formatter.format(localDateTime.toJavaLocalDateTime())
    }
}

/*return  DateTimeFormatter
            .ofPattern("dd/MM/yyyy - HH:mm", Locale.getDefault())
            .format(localDateTime.toJavaLocalDateTime())*/