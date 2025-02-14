package com.hashmato.retailtouch.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

object DateTimeUtils{

    val localDateTime by lazy  { Clock.System.now().toLocalDateTime(TimeZone.UTC) }

    fun getStartLocalDateTime():LocalDateTime{
        val startOfDay = LocalDateTime(
            year = localDateTime.year,
            month = localDateTime.month,
            dayOfMonth = localDateTime.dayOfMonth,
            hour = 0,
            minute = 0,
            second = 0,
            nanosecond = 0
        )
        return startOfDay
    }

    fun getEndLocalDateTime():LocalDateTime{
        val endOfDay = LocalDateTime(
            year = localDateTime.year,
            month = localDateTime.month,
            dayOfMonth = localDateTime.dayOfMonth,
            hour = 23,
            minute = 59,
            second = 59,
            nanosecond = 999999999
        )
        return endOfDay
    }

    fun formatLocalDateWithoutTimeForPrint(dateTime:LocalDateTime):String{
        val day = dateTime.dayOfMonth.toString().padStart(2, '0') // e.g., 01, 15
        val month = dateTime.month.name.lowercase().replaceFirstChar { it.uppercase() } // e.g., Jan, Feb
        val year = dateTime.year.toString() // e.g., 2024
        return "$day $month $year"
    }


    fun getCurrentDate() : String {
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        // Format the date as "YYYY-MM-DD"
        return currentDate.toString()
    }
    fun getCurrentLocalDate() : LocalDate {
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        // Format the date as "YYYY-MM-DD"
        return currentDate
    }

    fun getCurrentLocalDateTime() : LocalDateTime {
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        // Format the date as "YYYY-MM-DD"
        return currentDate
    }

    fun getCurrentTime() : String {
        val currentTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val hours = currentTime.hour.toString().padStart(2, '0')
        val minutes = currentTime.minute.toString().padStart(2, '0')
        return "$hours : $minutes"
    }


    fun getCurrentDateAndTimeInEpochMilliSeconds(): Long {
        return Clock.System.now().toEpochMilliseconds() // Directly get epoch milliseconds
    }

    fun getHoursDifferenceFromEpochMillSeconds(startTime: Long, currentTime: Long): Long {
        val startInstant = Instant.fromEpochMilliseconds(startTime)
        val currentInstant = Instant.fromEpochMilliseconds(currentTime)
        val durationDiff = currentInstant.minus(startInstant).inWholeHours
        return durationDiff
    }

    fun getHoursDifferenceFromEpochMilliseconds(startTime: Long, currentTime: Long): Long {
        return (currentTime - startTime) / (1000 * 60 * 60) // Convert milliseconds to hours
    }


    fun getCurrentDateTime(): String {
        // Get the current date
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        val currentDateTime ="${now.year % 100}" +
                now.monthNumber.toString().padStart(2, '0') +
                now.dayOfMonth.toString().padStart(2, '0') +
                now.hour.toString().padStart(2, '0') +
                now.minute.toString().padStart(2, '0') +
                now.second.toString().padStart(2, '0') +
                "${now.nanosecond / 1_000_000}"

        return currentDateTime
    }

    fun getLastSyncDateTime(): String {
        val lastSyncDateTime = Clock.System.now().minus(2, DateTimeUnit.DAY, TimeZone.UTC)
        return lastSyncDateTime.toString()
    }

    fun String?.parseDateFromApi(): String {
        return try {
            if (this.isNullOrEmpty()) {
                "1970-01-01 00:00:00"  // Return Epoch as string
            } else {
                val newStr = this.substring(0, 10) + " " + this.substring(11, 19) + ".000"
                val dateTime = LocalDateTime.parse(newStr.replace(" ", "T"))
                val instant = dateTime.toInstant(TimeZone.currentSystemDefault())
                instant.toLocalDateTime(TimeZone.currentSystemDefault()).toString()
            }
        } catch (e: Exception) {
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
        }
    }

    fun String?.getDateFromApi(): String {
        return try {
            // Check if the string is not null or empty
            if (this.isNullOrEmpty()) {
                "1970-01-01 00:00:00"  // Return Epoch as string if input is null or empty
            } else {
                // Remove the "Z" and parse the date-time string
                val newStr = this.substring(0, 10) + " " + this.substring(11, 19)
                val dateTime = LocalDateTime.parse(newStr.replace(" ", "T"))

                // Extract day, month, and year from the LocalDateTime
                val day = dateTime.dayOfMonth.toString().padStart(2, '0')  // Ensures two digits
                val month = dateTime.monthNumber.toString().padStart(2, '0') // Ensures two digits
                val year = dateTime.year.toString() // Four digits

                // Return the formatted string in "dd.MM.yyyy" format
                return "$day.$month.$year"
            }
        } catch (e: Exception) {
            // In case of error, return the current date in "dd.MM.yyyy" format
            val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val day = currentDate.dayOfMonth.toString().padStart(2, '0')
            val month = currentDate.monthNumber.toString().padStart(2, '0')
            val year = currentDate.year.toString()
            return "$day.$month.$year"
        }
    }

    fun formatDateTimeFromApiString(apiDate: String?): String {
        return try {
            if (apiDate.isNullOrEmpty()) {
                // Return the current date and time formatted as "dd.MM.yyyy HH:mm"
                val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                "${currentDateTime.dayOfMonth.toString().padStart(2, '0')}-" +
                        "${currentDateTime.monthNumber.toString().padStart(2, '0')}-" +
                        "${currentDateTime.year} " +
                        "${currentDateTime.hour.toString().padStart(2, '0')}:" +
                        currentDateTime.minute.toString().padStart(2, '0')
            } else {
                // Reformat the input string
                val newStr = apiDate.substring(0, 10) + "T" + apiDate.substring(11, 19)
                // Parse the date and time
                val parsedDateTime = LocalDateTime.parse(newStr)
                // Format the parsed date and time as "dd.MM.yyyy HH:mm"
                "${parsedDateTime.dayOfMonth.toString().padStart(2, '0')}-" +
                        "${parsedDateTime.monthNumber.toString().padStart(2, '0')}-" +
                        "${parsedDateTime.year} " +
                        "${parsedDateTime.hour.toString().padStart(2, '0')}:" +
                        parsedDateTime.minute.toString().padStart(2, '0')
            }
        } catch (e: Exception) {
            // Return the current date and time in case of error
            val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            "${currentDateTime.dayOfMonth.toString().padStart(2, '0')}-" +
                    "${currentDateTime.monthNumber.toString().padStart(2, '0')}-" +
                    "${currentDateTime.year} " +
                    "${currentDateTime.hour.toString().padStart(2, '0')}:" +
                    currentDateTime.minute.toString().padStart(2, '0')
        }
    }

    fun parseDateTimeFromApiStringUTC(apiDate: String?): LocalDateTime {
        return try {
            if (apiDate.isNullOrEmpty()) {
                // Return the Unix epoch start as a default
                Instant.fromEpochMilliseconds(0).toLocalDateTime(TimeZone.UTC)
            } else {
                // Parse the date string as an Instant
                val parsedInstant = Instant.parse(apiDate)
                // Convert to the local time zone
                parsedInstant.toLocalDateTime(TimeZone.currentSystemDefault())
            }
        } catch (e: Exception) {
            // Return the current local date-time in case of an error
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        }
    }

    fun parseDateFromApiUTC(dateString: String?): LocalDate {
        return try {
            if (dateString != null) {
                // Parse the ISO 8601 date-time string
                val instant = Instant.parse(dateString)
                // Convert to LocalDateTime and extract LocalDate (ignores time)
                instant.toLocalDateTime(TimeZone.UTC).date
            } else {
                // Return the current local date if the input is null
                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            }
        } catch (e: Exception) {
            // Return the current local date in case of an exception
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        }
    }

    fun LocalDate.formatLocalDateToDDMMYYYY(): String {
        val day = this.dayOfMonth.toString().padStart(2, '0')
        val month = this.monthNumber.toString().padStart(2, '0')
        val year = this.year.toString()
        return "$day.$month.$year"
    }

    fun formatDateForUI(localDateTime: LocalDateTime?): String {

        // Use the current date and time if localDateTime is null
        val dateTimeToFormat = localDateTime ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        // Map of month numbers to names
        val monthNames = listOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )
        val day = dateTimeToFormat.dayOfMonth
        val month = monthNames[dateTimeToFormat.monthNumber - 1] // Convert 1-based month to name
        val year = dateTimeToFormat.year
        val hour = dateTimeToFormat.hour.toString().padStart(2, '0') // Ensure two digits
        val minute = dateTimeToFormat.minute.toString().padStart(2, '0') // Ensure two digits

        // Format as "3 April 2024 06:17"
        return "$day $month $year $hour:$minute"
    }

    fun getEpochTimestamp(): Long {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
    }
    fun getDateTimeFromEpochMillSeconds(epochTime: Long): LocalDate {
        val epochInstant = Instant.fromEpochMilliseconds(epochTime)
        return epochInstant.toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

    fun formatMillisecondsToDateTime(milliseconds: Long): LocalDateTime {
        // Convert milliseconds to LocalDateTime
        val instant = Instant.fromEpochMilliseconds(milliseconds)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return localDateTime
    }

}