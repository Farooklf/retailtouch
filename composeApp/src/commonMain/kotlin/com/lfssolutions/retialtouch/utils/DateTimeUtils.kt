package com.lfssolutions.retialtouch.utils

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
    fun getCurrentFormattedDate(): String {
        val currentMoment: Instant = Clock.System.now()
        val dateTime: LocalDateTime = currentMoment.toLocalDateTime(TimeZone.UTC)
        return dateTime.toString()
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
        return "$hours $minutes"
    }

    /*fun getCurrentDateAndTimeInEpochMilliSeconds(): Long {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toInstant(
            TimeZone.currentSystemDefault()).toEpochMilliseconds()
    }*/

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

    fun getLastSyncDateTime(): Instant {
        val lastSyncDateTime = Clock.System.now().minus(2, DateTimeUnit.DAY, TimeZone.UTC)
        return lastSyncDateTime
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


    // Helper function to format LocalDateTime to "yyyy-MM-dd HH:mm:ss".
    private fun LocalDateTime.toFormattedString(): String {
        val year = this.year.toString().padStart(4, '0')
        val month = this.monthNumber.toString().padStart(2, '0')
        val day = this.dayOfMonth.toString().padStart(2, '0')
        val hour = this.hour.toString().padStart(2, '0')
        val minute = this.minute.toString().padStart(2, '0')
        val second = this.second.toString().padStart(2, '0')

        return "$year-$month-$day $hour:$minute:$second"
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


    fun parseDateStringToMillis(dateString: String?): Long? {
        // Ensure the date is in the "dd-MM-yyyy HH:mm.000" format
        if(dateString.isNullOrEmpty()){
            return null
        }
        println("Date :$dateString")

        val parts = dateString.split(" ")

        println("parts :$parts")

        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val day = parts[0].toInt()
        val month = monthNames.indexOf(parts[1])
        val year = parts[2].toInt()

        // Split the time and AM/PM
        val timeParts = parts[3].split(":")
        var hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()
        val amPm = parts[4]          // AM/PM

        // Handle AM/PM for 12-hour format
        if (amPm == "PM" && hour < 12) {
            hour += 12
        } else if (amPm == "AM" && hour == 12) {
            hour = 0
        }

        // Create the LocalDateTime object
        val localDateTime = LocalDateTime(year, month + 1, day, hour, minute)

        // Convert LocalDateTime to Instant using the system's default time zone
        val instant = localDateTime.toInstant(TimeZone.currentSystemDefault())

        // Return the milliseconds since the epoch
        return instant.toEpochMilliseconds()
    }

    fun parseDateStringToMillis2(dateString: String?): Long? {
        // Ensure the date is in the "dd-MM-yyyy HH:mm.000" format
        if(dateString.isNullOrEmpty()){
            return null
        }
        println("Date :$dateString")
        val parts = dateString.split(" ")
        val dateParts = parts[0].split("-")
        val timeParts = parts[1].split(":")
        println("parts :$parts | dateParts : $dateParts |  timeParts : $timeParts")

        val day = dateParts[0].toInt()
        val month = dateParts[1].toInt()
        val year = dateParts[2].toInt()

        val hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()

        // Create and return the LocalDateTime
        val localDateTime = LocalDateTime(year, month, day, hour, minute)
        // Convert LocalDateTime to Instant using the system's default time zone
        val instant = localDateTime.toInstant(TimeZone.currentSystemDefault())

        // Return the milliseconds since the epoch

        return instant.toEpochMilliseconds()
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

    fun getCurrentDateTimeInPreferredFormat(): String {
        // Get the current date and time in the system's default time zone
        val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

        // Format it as "yyyy-MM-dd HH:mm:ss.000"
        val year = currentDateTime.year
        val month = currentDateTime.monthNumber.toString().padStart(2, '0')
        val day = currentDateTime.dayOfMonth.toString().padStart(2, '0')
        val hour = currentDateTime.hour.toString().padStart(2, '0')
        val minute = currentDateTime.minute.toString().padStart(2, '0')
        //val second = currentDateTime.second.toString().padStart(2, '0')

        return "$day-$month-$year $hour:$minute"
    }

    fun convertApiDateTimeToString(inputDateTime: String?): String {
        if (inputDateTime.isNullOrEmpty()) {
            // Return the current date and time formatted as "dd.MM.yyyy HH:mm"
            val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            return "${currentDateTime.dayOfMonth.toString().padStart(2, '0')}-" +
                    "${currentDateTime.monthNumber.toString().padStart(2, '0')}-" +
                    "${currentDateTime.year} " +
                    "${currentDateTime.hour.toString().padStart(2, '0')}:" +
                    currentDateTime.minute.toString().padStart(2, '0')
        }else{
            // Parse the input string into an Instant
            val instant = Instant.parse(inputDateTime)

            // Convert the Instant to LocalDateTime in your preferred timezone (UTC in this case)
            val localDateTime = instant.toLocalDateTime(TimeZone.UTC)

            // Extract the day, month, year, hour, and minute
            val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
            val month = localDateTime.monthNumber.toString().padStart(2, '0')
            val year = localDateTime.year
            val hour = localDateTime.hour.toString().padStart(2, '0')
            val minute = localDateTime.minute.toString().padStart(2, '0')

            // Return the formatted string in dd-MM-yyyy HH:mm format
            return "$day-$month-$year $hour:$minute"
        }
    }

    fun getCurrentDateTimeAsddMMMYYYY(): String {
        // Get current date and time in UTC and convert to local time zone
        val currentInstant = Clock.System.now()
        val localDateTime = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault())

        // Extract components
        val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val month = monthNames[localDateTime.monthNumber - 1]
        val year = localDateTime.year
        val hour = (localDateTime.hour % 12).let { if (it == 0) 12 else it } // Convert to 12-hour format
        val minute = localDateTime.minute.toString().padStart(2, '0')
        val period = if (localDateTime.hour < 12) "AM" else "PM"

        // Format as "dd MMM yyyy hh:mm tt"
        return "$day $month $year $hour:$minute $period"
    }

    fun parseDateFromApiString(input: String?): String {
        val instant = if (input.isNullOrEmpty()) {
            Clock.System.now() // Use the current instant if input is null or empty
        } else {
            Instant.parse(input)
        }

        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

        // Extract components
        val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val month = monthNames[localDateTime.monthNumber - 1]
        val year = localDateTime.year
        val hour = (localDateTime.hour % 12).let { if (it == 0) 12 else it } // Convert to 12-hour format
        val minute = localDateTime.minute.toString().padStart(2, '0')
        val period = if (localDateTime.hour < 12) "AM" else "PM"

        // Format as "dd MMM yyyy hh:tt"
        return "$day $month $year $hour:$minute $period"
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

    fun getCurrentDateAsDDMMYYYY(): String {
        // Get the current date in the local time zone
        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        // Extract components
        val day = currentDate.dayOfMonth.toString().padStart(2, '0')
        val month = currentDate.monthNumber.toString().padStart(2, '0')
        val year = currentDate.year

        // Format as "dd.MM.yyyy"
        return "$day.$month.$year"
    }

    fun parseYYYYMMDDToLocalDate(dateString: String): LocalDate? {
        return try {
            // Split the string into day, month, and year
            val parts = dateString.split("-")
            val day = parts[0].toInt()
            val month = parts[1].toInt()
            val year = parts[2].toInt()

            // Create a LocalDate object
            LocalDate(year, month, day)
        } catch (e: Exception) {
            null // Return null if parsing fails
        }
    }

    fun getEpochTimestamp(): Long {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
    }
    fun getDateTimeFromEpochMillSeconds(epochTime: Long): LocalDate {
        val epochInstant = Instant.fromEpochMilliseconds(epochTime)
        return epochInstant.toLocalDateTime(TimeZone.currentSystemDefault()).date
    }

}