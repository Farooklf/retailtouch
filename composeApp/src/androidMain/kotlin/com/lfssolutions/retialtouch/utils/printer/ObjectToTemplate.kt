package com.lfssolutions.retialtouch.utils.printer


import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Scanner


class ObjectToTemplate {
    companion object {
        private val gson = Gson()

        fun applyTemplate(template: String, obj: Any): String {
            var result = template
            val fields = obj::class.java.declaredFields
            val objectName = obj::class.simpleName
            for (field in fields) {
                field?.let {
                    field.isAccessible = true
                    field.get(obj)?.let {
                        val newValue = field.get(obj).toString()
                        val oldValue = "{${objectName}.${field.name}}"
                        Log.e(oldValue, newValue)
                        if (newValue.isNotEmpty()) {
                            if (field.type == List::class.java) {
                                val searchString = "##${objectName}.${field.name}##"
                                Log.e("ObjectTemplate",searchString)

                                val templateLines = result.split("\n")
                                val templateTitleIndex = templateLines.indexOf(searchString)
                                var listTemplate = ""

                                val nextLineIndex = templateTitleIndex + 1
                                val indicesToRemove = ArrayList<Int>()

                                if (templateTitleIndex != -1 && nextLineIndex < templateLines.size) {
                                    indicesToRemove.add(templateTitleIndex)
                                    indicesToRemove.add(nextLineIndex)

                                    listTemplate = templateLines[nextLineIndex]

                                    for (i in (nextLineIndex + 1) until templateLines.size) {
                                        if (templateLines[i].isEmpty()) {
                                            break
                                        }
                                        listTemplate += "\n${templateLines[i]}"
                                        indicesToRemove.add(i)
                                    }
                                }
                                result = templateLines.filterIndexed { index, _ -> index !in indicesToRemove }.joinToString("\n")

                                val listOldValue = "#${objectName}.${field.name}#"
                                var toBeReplacedString = ""
                                var insertNewLine = false

                                for (itemInList in field.get(obj) as List<*>) {
                                    if (itemInList != null) {
                                        toBeReplacedString += "${if (insertNewLine) "\n" else "" /* "${listOldValue}\n" */}${
                                            applyTemplate(
                                                listTemplate,
                                                itemInList
                                            )
                                        }"
                                        insertNewLine = true
                                    }
                                }

                                result = result.replace(listOldValue, toBeReplacedString)
                            }
                            else if (isJson(newValue)) {
                                Log.e("Json",newValue)
                                try {
                                    val fieldsInJson = gson.fromJson<Map<String, String>>(newValue, object : TypeToken<Map<String, String>>() {}.type)
                                    for (jsonField in fieldsInJson) {
                                        val jsonOldValue = "{${objectName}.${field.name}!${jsonField.key}}"
                                        val jsonNewValue = jsonField.value

                                        result = result.replace(jsonOldValue, jsonNewValue)
                                    }
                                    val pattern =
                                        "\\[[^}]+\\:\\{$objectName\\.${field.name}!.[^}]+\\}\\]".toRegex()
                                    result = result.replace(pattern, "")

                                    val regx = "\\{$objectName\\.${field.name}![^}]+\\}".toRegex()
                                    result = result.replace(regx, "")
                                    Log.e("Result", result)
                                } catch (ex: Exception) {
                                    Log.e("Parsing Error", ex.message.toString())
                                }
                            }
                            else if (isDecimalFormat(objectName, field.name, result)) {
                                val formatDecimalPattern =
                                    "\\[=FormatDecimal\\(\\{$objectName\\.${field.name}\\},(.*)\\)\\]".toRegex()
                                val formatDecimalMatch = formatDecimalPattern.find(result)
                                if (formatDecimalMatch != null) {
                                    Log.e("InsideFormatDecimal", newValue)
                                    val decimalPlaces =
                                        formatDecimalMatch.groups[1]?.value?.toIntOrNull()
                                    if (decimalPlaces != null) {
                                        result = result.replace(
                                            formatDecimalMatch.value,
                                            formatDouble(newValue, decimalPlaces)
                                        )
                                    } else {
                                    }
                                } else {
                                    result = result.replace(oldValue, newValue)
                                }
                            }
                            else if (isStringFormat(objectName, field.name, result)) {

                                var stringParts = result.split("\\s+".toRegex())
                                stringParts.forEach { strResult ->
                                    Log.e("Spliteed", strResult)
                                    if (strResult.contains("=FormatString")) {
                                        val formatStringPattern =
                                            "\\[=FormatString\\(\\{$objectName\\.${field.name}\\},(.*)\\)\\]".toRegex()
                                        val formatStringMatch = formatStringPattern.find(strResult)
                                        if (formatStringMatch != null) {


                                            val decimalPlaces =
                                                formatStringMatch.groups[1]?.value?.toIntOrNull()

                                            if (decimalPlaces != null) {
                                                Log.e(
                                                    "New Value",
                                                    formatString(newValue, decimalPlaces)
                                                )
                                                result = result.replace(
                                                    formatStringMatch.value,
                                                    formatString(newValue, decimalPlaces)
                                                )
                                            }
                                        } else {
                                            result = result.replace(oldValue, newValue)
                                        }
                                    }

                                }


                            }
                            else if (isDateFormat(objectName, field.name, result)) {
                                val formatDatePattern =
                                    "\\[=FormatDate\\(\\{$objectName\\.${field.name}\\},”(.*)”\\)\\]".toRegex()
                                val formatDateMatch = formatDatePattern.find(result)
                                if (formatDateMatch != null) {
                                    Log.e("InsideFormatDate", newValue)
                                    val dateFormat = formatDateMatch.groups[1]?.value
                                    var dateAndTimeInServerFormatString = field.get(obj).toString()
                                    var dateObject = getDateFromServerFormatDateStringFormat(
                                            dateAndTimeInServerFormatString
                                        )
                                    result = result.replace(
                                        formatDateMatch.value,
                                        SimpleDateFormat(dateFormat).format(dateObject)
                                    )
                                } else {
                                    result = result.replace(oldValue, newValue)
                                }
                            }
                            else {

//                            }else {
//                                val formatDecimalPattern =
//                                    "\\[=FormatDecimal\\(\\{$objectName\\.${field.name}\\},(.*)\\)\\]".toRegex()
//                                val formatDecimalMatch = formatDecimalPattern.find(result)
//
//                                val formatDatePattern =
//                                    "\\[=FormatDate\\(\\{$objectName\\.${field.name}\\},”(.*)”\\)\\]".toRegex()
//                                val formatDateMatch = formatDatePattern.find(result)
//
//                                val formatStringPattern =
//                                    "\\[=FormatString\\(\\{$objectName\\.${field.name}\\},(.*)\\)\\]".toRegex()
//                                val formatStringMatch = formatStringPattern.find(result)
//
//                                if (formatDecimalMatch != null) {
//                                    Log.e("InsideFormatDecimal",newValue)
//                                    val decimalPlaces =
//                                        formatDecimalMatch.groups[1]?.value?.toIntOrNull()
//                                    if (decimalPlaces != null) {
//                                        result = result.replace(
//                                            formatDecimalMatch.value,
//                                            formatDouble(newValue, decimalPlaces)
//                                        )
//                                    }
//                                } else if (formatDateMatch != null) {
//                                    Log.e("InsideFormatDate",newValue)
//                                    val dateFormat = formatDateMatch.groups[1]?.value
//                                    var dateAndTimeInServerFormatString = field.get(obj).toString()
//                                    var dateObject = DateTimeFormatter.getDateFromServerFormatDateStringFormat(dateAndTimeInServerFormatString)
//                                    result = result.replace(
//                                        formatDateMatch.value,
//                                        SimpleDateFormat(dateFormat).format(dateObject)
//                                    )
//                                }else if (formatStringMatch != null) {
//                                    val decimalPlaces =
//                                        formatStringMatch.groups[1]?.value?.toIntOrNull()
//
//                                    if (decimalPlaces != null) {
//                                        Log.e("New Value", formatString(newValue, decimalPlaces))
//                                        result = result.replace(
//                                            formatStringMatch.value,
//                                            formatString(newValue, decimalPlaces)
//                                        )
//                                    }
//                                } else {
                                result = result.replace(oldValue, newValue)
//                                }
                            }
                        } else {
                            result = result.replace(oldValue, "")
                        }
                    }?: kotlin.run {
                        val oldValue = "{${objectName}.${field.name}}"
                        result = result.replace(oldValue, "")
                    }
                }
            }

            // result = setExtraFields(result, extraFields)
            val listPattern = "#[^}]+\\#".toRegex()
            val fieldPattern = "\\{[^}]+\\}".toRegex()
            result = listPattern.replace(result, "")
            result = fieldPattern.replace(result, "")
//
//            result = result.replace("[", "").replace("]", "")
            println("final result $result")
            return result
        }

        fun removeEmptyLines(myString: String): String {
            val regex = Regex("\n\\s*\n")
            return myString.replace(regex, "")
        }

        private fun isDecimalFormat(objectName: String?, name: String, result: String): Boolean {
            val formatDecimalPattern =
                "\\[=FormatDecimal\\(\\{$objectName\\.${name}\\},(.*)\\)\\]".toRegex()
            val formatDecimalMatch = formatDecimalPattern.find(result)
            return formatDecimalMatch != null
        }

        private fun isStringFormat(objectName: String?, name: String, result: String): Boolean {
            val formatStringPattern =
                "\\[=FormatString\\(\\{$objectName\\.${name}\\},(.*)\\)\\]".toRegex()
            val formatDecimalMatch = formatStringPattern.find(result)
            return formatDecimalMatch != null
        }

        private fun isDateFormat(objectName: String?, name: String, result: String): Boolean {
            val formatDatePattern =
                "\\[=FormatDate\\(\\{$objectName\\.${name}\\},(.*)\\)\\]".toRegex()
            val formatDecimalMatch = formatDatePattern.find(result)
            return formatDecimalMatch != null
        }

        private fun formatString(s: String, n: Int): String {
            Log.e("FormatString", s)
            return if (s == null || s.length <= n) {
                s
            } else s.substring(0..n) + ".."
        }

        //Test Function
        fun applyTemplate(
            template: String,
            obj: Any,
            extraFields: HashMap<String, Any> = HashMap<String, Any>()
        ): String {
            var result = template
            val fields = obj::class.java.declaredFields
            val objectName = obj::class.simpleName

            for (field in fields) {
                field.isAccessible = true
                val newValue = field.get(obj).toString()
                val oldValue = "{${objectName}.${field.name}}"

                if (isJson(newValue)) {
                    val fieldsInJson = gson.fromJson<Map<String, String>>(
                        newValue,
                        object : TypeToken<Map<String, String>>() {}.type
                    )
                    for (jsonField in fieldsInJson) {
                        val jsonOldValue = "{${objectName}.${field.name}!${jsonField.key}}"
                        val jsonNewValue = jsonField.value

                        result = result.replace(jsonOldValue, jsonNewValue)
                    }
                    val pattern = "\\[[^}]+\\:\\{$objectName\\.${field.name}!.[^}]+\\}\\]".toRegex()
                    result = result.replace(pattern, "")

                    val regx = "\\{$objectName\\.${field.name}![^}]+\\}".toRegex()
                    result = result.replace(regx, "")
                } else if (field.type == List::class.java) {
                    val searchString = "##${objectName}.${field.name}##"

                    val templateLines = result.split("\n")
                    val templateTitleIndex = templateLines.indexOf(searchString)
                    var listTemplate = ""

                    val nextLineIndex = templateTitleIndex + 1
                    val indicesToRemove = ArrayList<Int>()

                    if (templateTitleIndex != -1 && nextLineIndex < templateLines.size) {
                        indicesToRemove.add(templateTitleIndex)
                        indicesToRemove.add(nextLineIndex)

                        listTemplate = templateLines[nextLineIndex]

                        for (i in (nextLineIndex + 1) until templateLines.size) {
                            if (templateLines[i].isEmpty()) {
                                break
                            }
                            listTemplate += "\n${templateLines[i]}"
                            indicesToRemove.add(i)
                        }
                    }
                    result = templateLines.filterIndexed { index, _ -> index !in indicesToRemove }
                        .joinToString("\n")

                    val listOldValue = "#${objectName}.${field.name}#"
                    var toBeReplacedString = ""
                    var insertNewLine = false

                    for (itemInList in field.get(obj) as List<*>) {
                        if (itemInList != null) {
                            toBeReplacedString += "${if (insertNewLine) "\n" else "" /* "${listOldValue}\n" */}${
                                applyTemplate(
                                    listTemplate,
                                    itemInList
                                )
                            }"
                            insertNewLine = true
                        }
                    }

                    result = result.replace(listOldValue, toBeReplacedString)
                } else {
                    val formatDecimalPattern =
                        "\\[=FormatDecimal\\(\\{$objectName\\.${field.name}\\},(.*)\\)\\]".toRegex()
                    val formatDecimalMatch = formatDecimalPattern.find(result)

                    val formatDatePattern =
                        "\\[=FormatDate\\(\\{$objectName\\.${field.name}\\},”(.*)”\\)\\]".toRegex()
                    val formatDateMatch = formatDatePattern.find(result)

                    val formatStringPattern =
                        "\\[=FormatString\\(\\{$objectName\\.${field.name}\\},”(.*)”\\)\\]".toRegex()
                    val formatStringMatch = formatStringPattern.find(result)

                    if (formatDecimalMatch != null) {
                        val decimalPlaces = formatDecimalMatch.groups[1]?.value?.toIntOrNull()
                        if (decimalPlaces != null) {
                            result = result.replace(
                                formatDecimalMatch.value,
                                formatDouble(newValue, decimalPlaces)
                            )
                        }
                    } else if (formatDateMatch != null) {
                        val dateFormat = formatDateMatch.groups[1]?.value
                        var dateAndTimeInServerFormatString = field.get(obj).toString()
                        var dateObject = getDateFromServerFormatDateStringFormat(
                            dateAndTimeInServerFormatString
                        )
                        result = result.replace(
                            formatDateMatch.value,
                            SimpleDateFormat(dateFormat).format(dateObject)
                        )
                    } else if (formatStringMatch != null) {
                        val decimalPlaces =
                            formatStringMatch.groups[1]?.value?.toIntOrNull()
                        if (decimalPlaces != null) {
                            result = result.replace(
                                formatStringMatch.value,
                                formatString(newValue, decimalPlaces)
                            )
                        }
                    } else {
                        result = result.replace(oldValue, newValue)
                    }
                }
            }
            Log.e("BeforeBeforeResult", result)
            result = setExtraFields(result, extraFields)
            Log.e("BeforeResult", result)
            val listPattern = "#[^}]+\\#".toRegex()
            val fieldPattern = "\\{[^}]+\\}".toRegex()
            result = listPattern.replace(result, "")
            result = fieldPattern.replace(result, "")

            //result = result.replace("[", "").replace("]", "")
            Log.e("AfterResult", result)
            return result
        }

        private fun setExtraFields(
            template: String,
            extraFields: HashMap<String, Any> = HashMap<String, Any>()
        ): String {
            var result = template

            for (extraField in extraFields) {
                val newValue = extraField.value.toString()
                val oldValue = "{${extraField.key}}"

                val formatDecimalPattern =
                    "\\[=FormatDecimal\\(\\{${extraField.key}\\},(.*)\\)\\]".toRegex()
                val formatDecimalMatch = formatDecimalPattern.find(result)

                val formatDatePattern =
                    "\\[=FormatDate\\(\\{${extraField.key}\\},”(.*)”\\)\\]".toRegex()
                val formatDateMatch = formatDatePattern.find(result)

                val formatStringPattern =
                    "\\[=FormatString\\(\\{${extraField.key}\\},(.*)\\)\\]".toRegex()
                val formatStringMatch = formatStringPattern.find(result)

                if (formatDecimalMatch != null) {
                    val decimalPlaces = formatDecimalMatch.groups[1]?.value?.toIntOrNull()
                    if (decimalPlaces != null) {
                        result = result.replace(
                            formatDecimalMatch.value,
                            formatDouble(newValue, decimalPlaces)
                        )
                    }
                } else if (formatDateMatch != null) {
                    val dateFormat = formatDateMatch.groups[1]?.value
                    result = result.replace(
                        formatDateMatch.value,
                        SimpleDateFormat(dateFormat).format(extraField.value)
                    )
                } else if (formatStringMatch != null) {
                    val decimalPlaces = formatStringMatch.groups[1]?.value?.toIntOrNull()
                    if (decimalPlaces != null) {
                        result = result.replace(
                            formatStringMatch.value,
                            formatString(newValue, decimalPlaces)
                        )
                    }
                } else {
                    result = result.replace(oldValue, newValue)
                }
            }

            return result
        }

        private fun formatDouble(value: String, decimalPlaces: Int): String {
            return "%.${decimalPlaces}f".format(value.toDouble())
        }

        private fun isJson(jsonString: String): Boolean {
            return try {
                val json = JsonParser().parse(jsonString)
                return json.isJsonObject || json.isJsonArray
            } catch (e: Exception) {
                false
            }
        }
        private fun findAndReplaceRemoveLine(line: String): String {

            val lineToRemove = "#removethisline"
            var newLine = ""
            val scanner = Scanner(line)

            scanner.forEach { byLine ->
                if(!byLine.contains(lineToRemove)){
                    newLine += byLine
                }
            }
            return  newLine
        }

    }

}

fun getDateFromServerFormatDateStringFormat(dateString: String): Date? {
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
    return formatter.parse(dateString)
}