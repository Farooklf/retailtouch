package com.lfssolutions.retialtouch.utils

import kotlinx.datetime.LocalDateTime

expect class DateFormatter() {

    fun getCurrentDateAndTimeInEpochMilliSeconds() : Long
    fun getHoursDifferenceFromEpochMilliseconds(savedTime: Long, currentTime: Long): Long
    fun formatDateWithTimeForApi(localDateTime: LocalDateTime): String
}