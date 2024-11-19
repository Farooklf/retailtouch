package com.lfsolutions.paymentslibrary.nets

import java.io.OutputStream
import java.math.BigDecimal
import java.net.Socket
import java.net.SocketTimeoutException
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NetsProcess {
    var merchantID = ""
    var terminalID = ""
    var cardNumber = ""
    var cardIssuerNameNAddress = ""
    var approvalCode = ""
    var transcationNumber = ""
    var cardIssuer = ""

    var responseText = ""
    var referanceText = ""
    var terminalCode = ""

    fun startNetsTransaction(netsIpPort: String, totalPrice: Double, networkProcessorTag: String) {
        var splied_ = netsIpPort.split(":")
        var ipAddress = ""
        var port = "3000"
        try {
            if (splied_.isNotEmpty()) {
                ipAddress = splied_[0]
                port = splied_[1]
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        val sendData = transInNetMachineNetwork(networkProcessorTag, BigDecimal(totalPrice))

        connectNetworkTerminal(
            ipAddress,
            port.toInt(),
            sendData
        ) { responseData ->
            processResponse(responseData)
        }

    }

    private fun connectNetworkTerminal(
        sIPAddress: String,
        nPortNo: Int,
        sSendData: String,
        response: (String?) -> Unit
    ): String? {
//        Log.e("SENDDATA", sSendData.replace(" ", ""))
//        storeLogToFirebase("SENDDATA", sSendData, "connectNetworkTerminal")
//        storeLogToFirebase("sIPAddress", sIPAddress, "connectNetworkTerminalsIPAddress")
//        storeLogToFirebase("nPortNo", nPortNo.toString(), "connectNetworkTerminalsnPortNo")
        return try {
            Socket(sIPAddress, nPortNo).use { client ->
                client.soTimeout = 1000 * 60
                val outputStream: OutputStream = client.getOutputStream()
                val bytes = sSendData.replace(" ", "").hexToByteArray()

                outputStream.write(bytes)

                val data = ByteArray(2000)
                val bytesRead = client.getInputStream().read(data)


                var responseData = data.toHexString()

                // response(responseData)
                // Log.e("Log responseData", responseData)
//                storeLogToFirebase(
//                    "LogResponseData",
//                    responseData,
//                    "connectNetworkTerminalResponse"
//                )
                response(responseData)
                responseData
            }
        } catch (e: SocketTimeoutException) {
            //socketTimeout = true
//            storeLogToFirebase(
//                "LogResponseData",
//                "SocketTimeoutException ${e.message}",
//                "connectNetworkTerminalResponse"
//            )
//            Log.e("Error", "Socket Timeout: ${e.message}")
            null

        } catch (e: Exception) {
//            storeLogToFirebase(
//                "LogResponseData",
//                "Exception ${e.message}",
//                "connectNetworkTerminalResponse"
//            )
            e.message?.let {
                //  Log.e("Error", it)
            }
            null
        }
    }

    fun transInNetMachineNetwork(sType: String, dAmount: BigDecimal): String {
        var sError: String = ""
        try {
            var sLength: String = ""
            var sFunctionCode: String = ""
            val sSTX = "02"
            val sETX = "03"
            var sHeaderData = ""
            var sMessageData = ""
            var sSendData: String = ""
            var sLrcData: String = ""
            val sReferenceNo = SimpleDateFormat("yyMMddHHmmss").format(Date())
            //Log.e("Date", sReferenceNo)
            when (sType) {
                "NETS" -> {
                    sLength = "00 84 "
                    sFunctionCode = "33 30 "
                    sHeaderData += sLength
                    sHeaderData += getStringInHex(sReferenceNo, 12)
                    sHeaderData += sFunctionCode
                    sHeaderData += "30 31 "
                    sHeaderData += "30 "
                    sHeaderData += "1C "
                    sMessageData += "54 32 00 02 30 31 1C "
                    sMessageData += "34 33 00 01 30 1C "
                    sMessageData += "34 30 00 12 " + getAmountInHex(dAmount, 12) + "1C "
                    sMessageData += "34 32 00 12 30 30 30 30 30 30 30 30 30 30 30 30 1C "
                    sMessageData += "48 44 00 13 " + getStringInHex(
                        sReferenceNo,
                        13
                    ) + "1C "
                }

                "CREDITCARD" -> {
                    sLength = "00 47 "
                    sFunctionCode = "49 30 "
                    sHeaderData += sLength
                    sHeaderData += getStringInHex(sReferenceNo, 12)
                    sHeaderData += sFunctionCode
                    sHeaderData += "30 31 "
                    sHeaderData += "30 "
                    sHeaderData += "1C "
                    sMessageData += "34 30 00 12 " + getAmountInHex(dAmount, 12) + "1C "
                    sMessageData += "39 47 00 06 " + getStringInHex("OCBC", 6) + "1C "
                }

                "NETSQR" -> {
                    sLength = "00 66 "
                    sFunctionCode = "33 30 "
                    sHeaderData += sLength
                    sHeaderData += getStringInHex(sReferenceNo, 12)
                    sHeaderData += sFunctionCode
                    sHeaderData += "30 31 "
                    sHeaderData += "30 "
                    sHeaderData += "1C "
                    sMessageData += "54 32 00 02 30 34 1C "
                    sMessageData += "34 33 00 01 30 1C "
                    sMessageData += "34 30 00 12 " + getAmountInHex(dAmount, 12) + "1C "
                    sMessageData += "34 32 00 12 30 30 30 30 30 30 30 30 30 30 30 30 1C "
//                    sMessageData += "48 44 00 13 " + getStringInHex(
//                        sReferenceNo,
//                        13
//                    ) + "1C "
                }

                "NETSFP" -> {
                    sLength = "00 51 "
                    sFunctionCode = "32 34 "
                    sHeaderData += sLength
                    sHeaderData += getStringInHex(sReferenceNo, 12)
                    sHeaderData += sFunctionCode
                    sHeaderData += "30 31 "
                    sHeaderData += "30 "
                    sHeaderData += "1C "
                    sMessageData += "34 30 00 12 " + getAmountInHex(dAmount, 12) + "1C "
                    sMessageData += "48 44 00 10 " + getStringInHex(
                        sReferenceNo.substring(0, 10),
                        10
                    ) + "1C "
                }

                "NETSCC" -> {
                    sLength = "00 36 "
                    sFunctionCode = "35 31 "
                    sHeaderData += sLength
                    sHeaderData += getStringInHex(sReferenceNo, 12)
                    sHeaderData += sFunctionCode
                    sHeaderData += "30 31 "
                    sHeaderData += "30 "
                    sHeaderData += "1C "
                    sMessageData += "34 30 00 12 " + getAmountInHex(dAmount, 12) + "1C "
                }

                "CREDITCARDOCBC" -> {
                    sLength = "00 65 "
                    sFunctionCode = "49 30 "
                    sHeaderData += sLength
                    sHeaderData += getStringInHex(sReferenceNo, 12)
                    sHeaderData += sFunctionCode
                    sHeaderData += "30 31 "
                    sHeaderData += "30 "
                    sHeaderData += "1C "
                    sMessageData += "34 30 00 12 " + getAmountInHex(dAmount, 12) + "1C "
                    //sMessageData += "39 47 00 10 30 30 00 06 55 4F 42 20 20 20 1C"
                    sMessageData += "39 47 00 10 30 30 30 30 30 30 30 30 30 30 1C "
                    var sReferenceNoShort = sReferenceNo
                    if (sReferenceNoShort.length > 10) sReferenceNoShort =
                        sReferenceNoShort.substring(0, 10)
                    sMessageData += "48 34 00 10 " + getStringInHex(
                        sReferenceNoShort,
                        10
                    ) + "1C "
                }

                "OCBC" -> {
                    sLength = "00 43 "
                    sHeaderData += sLength
                    sHeaderData += "50 "
                    sFunctionCode = "30 "
                    sHeaderData += sFunctionCode
                    sHeaderData += "56 31 38 "
                    sMessageData += getStringInHex(sReferenceNo, 20)
                    sMessageData += getAmountInHex(dAmount, 12)
                    sMessageData += "30 30 30 30 30 30 "
                }

                "NETSUOB" -> {
                    sLength = "00 77 "
                    sFunctionCode = "32 30 "
                    sHeaderData += sLength
                    sHeaderData += getStringInHex(sReferenceNo, 12)
                    sHeaderData += sFunctionCode
                    sHeaderData += "30 32 "
                    sHeaderData += "30 "
                    sHeaderData += "1C "
                    sHeaderData += "36 30 30 30 30 30 30 30 30 30 " //Header Filler
                    sHeaderData += "31 30 32 30 30 30 30 1C "
                    sMessageData += "34 30 00 12 " + getAmountInHex(dAmount, 12) + "1C "
                    //    sMessageData += "34 32 00 12 30 30 30 30 30 30 30 30 30 30 30 30 1C "
                    sMessageData += "44 38 00 19 " + getStringInHex(
                        sReferenceNo,
                        19
                    ) + "1C "

                }
            }

            sSendData = "$sHeaderData$sMessageData"
            sSendData = sSendData.replace(" ", "")
            //           Log.e("sSendData", sSendData.substring(4))
//            storeLogToFirebase(
//                "sSendData",
//                sSendData.substring(4),
//                "transInNetMachineNetworksSendData"
//            )
            sLrcData = getLRC(sSendData.substring(4))
            //  storeLogToFirebase("sLrcData", sLrcData, "transInNetMachineNetworkssLrcData")
            sSendData = "$sSendData$sLrcData"
            return sSendData
        } catch (ex: Exception) {
            sError = ex.message.toString()
//            requireActivity().runOnUiThread {
//                Toast.makeText(
//                    requireContext(),
//                    "Error ${getNetsMessage(sError.toString())}",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
        }

        return ""
    }

    fun transInNetMachine(sType: String, dAmount: BigDecimal): String {
        var sError: String = ""
        try {
            var sLength: String = ""
            var sFunctionCode: String = ""
            val sSTX = "02"
            val sETX = "03"
            var sHeaderData = ""
            var sMessageData = ""
            var sSendData: String = ""
            var sLrcData: String = ""
            val sReferenceNo = SimpleDateFormat("yyMMddHHmmss").format(Date())
            //Log.e("Date", sReferenceNo)
            when (sType) {
                "NETS" -> {
                    sLength = "00 83 "
                    sFunctionCode = "33 30 "
                    sHeaderData += sLength
                    sHeaderData += getStringInHex(sReferenceNo, 12)
                    sHeaderData += sFunctionCode
                    sHeaderData += "30 31 "
                    sHeaderData += "30 "
                    sHeaderData += "1C "
                    sMessageData += "54 32 00 02 30 31 1C "
                    sMessageData += "34 33 00 01 30 1C "
                    sMessageData += "34 30 00 12 " + getAmountInHex(dAmount, 12) + "1C "
                    sMessageData += "34 32 00 12 30 30 30 30 30 30 30 30 30 30 30 30 1C "
                    sMessageData += "48 44 00 13 " + getStringInHex(
                        sReferenceNo,
                        13
                    ) + "1C "
                }

                "NETSQR" -> {
                    sLength = "00 83 "
                    sFunctionCode = "33 30 "
                    sHeaderData += sLength
                    sHeaderData += getStringInHex(sReferenceNo, 12)
                    sHeaderData += sFunctionCode
                    sHeaderData += "30 31 "
                    sHeaderData += "30 "
                    sHeaderData += "1C "
                    sMessageData += "54 32 00 02 30 34 1C "
                    sMessageData += "34 33 00 01 30 1C "
                    sMessageData += "34 30 00 12 " + getAmountInHex(dAmount, 12) + "1C "
                    sMessageData += "34 32 00 12 30 30 30 30 30 30 30 30 30 30 30 30 1C "
                    sMessageData += "48 44 00 13 " + getStringInHex(
                        sReferenceNo,
                        13
                    ) + "1C "
                }

                "NETSFP" -> {
                    sLength = "00 35 "
                    sFunctionCode = "32 34 "
                    sHeaderData += sLength
                    sHeaderData += getStringInHex(sReferenceNo, 12)
                    sHeaderData += sFunctionCode
                    sHeaderData += "30 31 "
                    sHeaderData += "30 "
                    sHeaderData += "1C "
                    sMessageData += "34 30 00 12 " + getAmountInHex(dAmount, 12) + "1C "
                }

                "NETSCC" -> {
                    sLength = "00 35 "
                    sFunctionCode = "35 31 "
                    sHeaderData += sLength
                    sHeaderData += getStringInHex(sReferenceNo, 12)
                    sHeaderData += sFunctionCode
                    sHeaderData += "30 31 "
                    sHeaderData += "30 "
                    sHeaderData += "1C "
                    sMessageData += "34 30 00 12 " + getAmountInHex(dAmount, 12) + "1C "
                }

                "CREDITCARDOCBC" -> {
                    sLength = "00 65 "
                    sFunctionCode = "49 30 "
                    sHeaderData += sLength
                    sHeaderData += getStringInHex(sReferenceNo, 12)
                    sHeaderData += sFunctionCode
                    sHeaderData += "30 31 "
                    sHeaderData += "30 "
                    sHeaderData += "1C "
                    sMessageData += "34 30 00 12 " + getAmountInHex(dAmount, 12) + "1C "
                    //sMessageData += "39 47 00 10 30 30 00 06 55 4F 42 20 20 20 1C"
                    sMessageData += "39 47 00 10 30 30 30 30 30 30 30 30 30 30 1C "
                    var sReferenceNoShort = sReferenceNo
                    if (sReferenceNoShort.length > 10) sReferenceNoShort =
                        sReferenceNoShort.substring(0, 10)
                    sMessageData += "48 34 00 10 " + getStringInHex(
                        sReferenceNoShort,
                        10
                    ) + "1C "
                }

                "OCBC" -> {
                    sLength = "00 43 "
                    sHeaderData += sLength
                    sHeaderData += "50 "
                    sFunctionCode = "30 "
                    sHeaderData += sFunctionCode
                    sHeaderData += "56 31 38 "
                    sMessageData += getStringInHex(sReferenceNo, 20)
                    sMessageData += getAmountInHex(dAmount, 12)
                    sMessageData += "30 30 30 30 30 30 "
                }

                "NETSUOB" -> {
                    sLength = "00 77 "
                    sFunctionCode = "32 30 "
                    sHeaderData += sLength
                    sHeaderData += getStringInHex(sReferenceNo, 12)
                    sHeaderData += sFunctionCode
                    sHeaderData += "30 32 "
                    sHeaderData += "30 "
                    sHeaderData += "1C "
                    sHeaderData += "36 30 30 30 30 30 30 30 30 30 " //Header Filler
                    sHeaderData += "31 30 32 30 30 30 30 1C "
                    sMessageData += "34 30 00 12 " + getAmountInHex(dAmount, 12) + "1C "
                    //    sMessageData += "34 32 00 12 30 30 30 30 30 30 30 30 30 30 30 30 1C "
                    sMessageData += "44 38 00 19 " + getStringInHex(
                        sReferenceNo,
                        19
                    ) + "1C "

                }
            }

            sSendData = "$sHeaderData$sMessageData$sETX"
            sLrcData = getLRC(sSendData.replace(" ", ""))
            sSendData = "$sSTX $sSendData $sLrcData"
            return sSendData
        } catch (ex: Exception) {
            sError = ex.message.toString()
//            showErrorMessage(
//                "Error -- ${getNetsMessage(sError.toString())}"
//            )
        }

        return ""
    }

    private fun getAmountInHex(dAmount: BigDecimal, nLength: Int): String {
        var sHex = ""
        var sAmount = formatNumber(dAmount).replace(".", "")
        sAmount = sAmount.padStart(nLength, '0')
        val asAmountBytes = sAmount.toByteArray(StandardCharsets.US_ASCII)
        for (nCnt in asAmountBytes.indices) sHex += "${
            asAmountBytes[nCnt].toInt().and(0xFF).toString(16).toUpperCase()
        } "
        return sHex
    }

    private fun processResponse(responseData: String?) {
        responseData?.let {
            if (responseData.length > 40)
                responseData?.let {
                    if (isNETSSuccessReadResponse(
                            getStringFromHex(
                                responseData
                            )
                        )
                    ) {

//                        CoroutineScope(Dispatchers.Main).launch {
//                            storeLogToFirebase(
//                                "LogResponseData",
//                                it,
//                                "sucessNetworkTerminalResponse"
//                            )}
                        fillResponseData(it)
                        //  submitOrders()

                    } else {
//                        CoroutineScope(Dispatchers.Main).launch {
//                            val failureMsg = getResponseText(it)
//                            showPaymentError(failureMsg)
//                        }
                    }
                }
                    ?: kotlin.run {
//                        CoroutineScope(Dispatchers.Main).launch {
//                            Constant.isPaymentProcessing = false
//                            Constant.lastTouchActionDateTime = Date()
//
//                            viewCustom.findViewById<RelativeLayout>(R.id.loader_rl).visibility =
//                                View.GONE
//                            Toast.makeText(
//                                requireContext(),
//                                "Host device unreachable , please try again",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
                    }
        } ?: kotlin.run {
//            CoroutineScope(Dispatchers.Main).launch {
//                Constant.isPaymentProcessing = false
//                Constant.lastTouchActionDateTime = Date()
//                viewCustom.findViewById<RelativeLayout>(R.id.loader_rl).visibility = View.GONE
//                Toast.makeText(
//                    requireContext(),
//                    "Host device unreachable , please try again",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
        }
    }

    private fun isNETSSuccessReadResponse(response: String): Boolean {
        try {
            var hc = response.substring(17, 19)
            if (hc == "00") return true
        } catch (ex: Exception) {
            ex.message?.let { //Log.e("Error", it)
            }

        }
        return false
    }

    private fun formatNumber(dTemp: BigDecimal): String {
        return try {
            String.format("%.2f", dTemp)
        } catch (e: Exception) {
            ""
        }
    }

    private fun getStringInHex(sReferenceNo: String, nLength: Int): String {
        var sHex = ""
        var sReferenceNoPadded = sReferenceNo.padStart(nLength, '0')
        val asAmountBytes = sReferenceNoPadded.toByteArray(StandardCharsets.US_ASCII)
        for (nCnt in asAmountBytes.indices) sHex += "${
            asAmountBytes[nCnt].toInt().and(0xFF).toString(16).uppercase(Locale.US)
        } "
        return sHex
    }

    private fun getLRC(data: String): String {
        //Log.e("LRC STring", data)
        var checksum = 0
        getStringFromHex(data).forEach { c -> checksum = checksum.xor(c.toInt()) }
        return checksum.toString(16).uppercase(Locale.US).padStart(2, '0')
    }

    private fun getStringFromHex(s: String): String {
        val result = StringBuilder()
        val s2 = s.replace(" ", "")
        var i = 0
        while (i < s2.length) {
            result.append(Integer.parseInt(s2.substring(i, i + 2), 16).toChar())
            i += 2
        }
        return result.toString()
    }

    fun getNetsMessage(sResponseCode: String): String {
        var sErrorMsg = ""
        when (sResponseCode) {
            "01" -> sErrorMsg = "REFER TO NETS"
            "02" -> sErrorMsg = "REFER TO BANK"
            "03" -> sErrorMsg = "INVALID TERMINAL"
            "12" -> sErrorMsg = "INVALID TRANS"
            "13" -> sErrorMsg = "INVALID AMOUNT"
            "14" -> sErrorMsg = "INVALID CARD"
            "19" -> sErrorMsg = "RE-ENTER TRANSACTION"
            "25" -> sErrorMsg = "NO RECORD ON FILE"
            "30" -> sErrorMsg = "REFER TO NETS"
            "31", "42", "43" -> sErrorMsg = "INVALID CARD"
            "51" -> sErrorMsg = "DECLINED"
            "54" -> sErrorMsg = "EXPIRED"
            "55" -> sErrorMsg = "INCORRECT PIN"
            "58" -> sErrorMsg = "INVALID TRANSACTION"
            "61" -> sErrorMsg = "DAILY LIMIT EXCEEDED"
            "62" -> sErrorMsg = "INVALID TRANSACTION"
            "63" -> sErrorMsg = "VOID IMPOSSIBLE"
            "64" -> sErrorMsg = "TXN ALREADY VOID"
            "65" -> sErrorMsg = "VOID IMPOSSIBLE"
            "75" -> sErrorMsg = "PIN ERROR, REFER TO BANK"
            "76", "86" -> sErrorMsg = "DECLINED"
            "78" -> sErrorMsg = "INVALID CARD"
            "79" -> sErrorMsg = "SUPERVISOR PIN ERROR"
            "80", "81" -> sErrorMsg = "INVALID CARD"
            "82" -> sErrorMsg = "PIN ERROR, REFER TO NETS"
            "85" -> sErrorMsg = "INVALID CARD"
            "87" -> sErrorMsg = "DAILY LIMIT EXCEEDED"
            "88" -> sErrorMsg = "NO MERCH RET"
            "89" -> sErrorMsg = "INVALID TERMINAL"
            "91" -> sErrorMsg = "NO REPLY FROM BANK"
            "98" -> sErrorMsg = "MAC ERROR"
            "IM" -> sErrorMsg = "UNAUTHORIZED RESPONSE"
            "IR" -> sErrorMsg = "INVALID HOST MESSAGE"
            "IT" -> sErrorMsg = "INVALID TERMINAL"
            "IA" -> sErrorMsg = "INVALID HOST AMOUNT"
            "IC" -> sErrorMsg = "INVALID CARD"
            "IL" -> sErrorMsg = "INVALID DATA LENGTH"
            "HS" -> sErrorMsg = "CONNECT PROBLEM TO HOST"
            "TO" -> sErrorMsg = "TIMEOUT-PLEASE TRY AGAIN"
            "US" -> sErrorMsg = "CANCELLED BY USER"
            "BF" -> sErrorMsg = "TRANSACTION BATCH FULL"
            "SC" -> sErrorMsg = "CASHCARD TRANSACTION UNSUCCESSFUL"
            "N0" -> sErrorMsg = "DIFFERENT ISSUERS FOR A NETS SALE OR REVALUATION"
            "N1" -> sErrorMsg = "CASHCARD BLACKLIST"
            "N2" -> sErrorMsg = "BATCH ALREADY UPLOADED"
            "N3" -> sErrorMsg = "RESEND BATCH"
            "N4" -> sErrorMsg = "CASHCARD NOT FOUND"
            "N5" -> sErrorMsg = "EXPIRED"
            "N6" -> sErrorMsg = "REFUNDED CASHCARD"
            "N7" -> sErrorMsg = "CERTIFICATE ERROR"
            "N8" -> sErrorMsg = "INSUFFICIENT FUNDS/BALANCE"
            "IS" -> sErrorMsg = "INVALID STAN*"
            "RN" -> sErrorMsg = "RECORD NOT FOUND"
            "RE" -> sErrorMsg = "READER NOT READY"
            "T1" -> sErrorMsg = "INVALID TOP-UP CARD"
            "T2" -> sErrorMsg = "TERMINAL TOP-UP LIMIT EXCEEDED"
            "T3" -> sErrorMsg = "RETAILER LIMIT EXCEEDED"
            "LR" -> sErrorMsg = "MANUAL LOGON REQUIRED**"
            "DK" -> sErrorMsg = "BLOCKLIST DOWNLOAD REQUIRED"
            "DS" -> sErrorMsg = "CASHCARD SETTLEMENT REQUIRED"
            "BP" -> sErrorMsg = "CARD BLOCKED"
            "BA" -> sErrorMsg = "TRANSACTION BLOCKED, to prevent Autoload"
            "GB" -> sErrorMsg = "Golden Bullet Card"
            "R " -> sErrorMsg = "Payment Terminal - Out of Paper"
            else -> sErrorMsg = "Other"
        }
        return sErrorMsg
    }

    fun ByteArray.toHexString() =
        asUByteArray().joinToString("") { it.toString(16).padStart(2, '0') }

    fun String.hexToByteArray(): ByteArray? {
        var hex = this
        hex = if (hex.length % 2 != 0) "0$hex" else hex
        val b = ByteArray(hex.length / 2)
        for (i in b.indices) {
            val index = i * 2
            val v = hex.substring(index, index + 2).toInt(16)
            b[i] = v.toByte()
        }
        return b
    }

    private fun fillResponseData(sReceiveData: String): Boolean {
        try {
            var sTemp: String = ""
            //Log.e("paymentcar", sReceiveData)

            if (sReceiveData.indexOf("3032004") != -1) {
                val startIndex = sReceiveData.indexOf("3032004") + 8
                val endIndex = startIndex + 80

                sTemp = getStringFromHex(sReceiveData.substring(startIndex, endIndex))
                responseText = sTemp
            }
            if (sReceiveData.indexOf("44330012") != -1) {
                val startIndex = sReceiveData.indexOf("44330012") + 8
                val endIndex = startIndex + 24
                sTemp = getStringFromHex(sReceiveData.substring(startIndex, endIndex))
                referanceText = sTemp
            }
            if (sReceiveData.indexOf("44310015") != -1) {
                val startIndex = sReceiveData.indexOf("44310015") + 8
                val endIndex = startIndex + 30
                sTemp = getStringFromHex(sReceiveData.substring(startIndex, endIndex))
                merchantID = sTemp
            }
            if (sReceiveData.indexOf("31360008") != -1) {
                val startIndex = sReceiveData.indexOf("31360008") + 8
                val endIndex = startIndex + 16

                sTemp = getStringFromHex(sReceiveData.substring(startIndex, endIndex))
                terminalID = sTemp
            }
            if (sReceiveData.indexOf("33300016") != -1) {
                val startIndex = sReceiveData.indexOf("33300016") + 8
                val endIndex = startIndex + 32
                sTemp = getStringFromHex(sReceiveData.substring(startIndex, endIndex))
                cardNumber = sTemp
            }
            if (sReceiveData.indexOf("44320010") != -1) {
                val startIndex = sReceiveData.indexOf("44320010") + 8
                val endIndex = startIndex + 20

                sTemp = getStringFromHex(sReceiveData.substring(startIndex, endIndex))
                cardIssuerNameNAddress = sTemp
            }
            if (sReceiveData.indexOf("30310006") != -1) {
                val startIndex = sReceiveData.indexOf("30310006") + 8
                val endIndex = startIndex + 12

                sTemp = getStringFromHex(sReceiveData.substring(startIndex, endIndex))
                approvalCode = sTemp
            }
            if (sReceiveData.indexOf("36350006") != -1) {
                val startIndex = sReceiveData.indexOf("36350006") + 8
                val endIndex = startIndex + 12

                sTemp = getStringFromHex(sReceiveData.substring(startIndex, endIndex))
                transcationNumber = sTemp
            }
            cardIssuer = cardIssuerNameNAddress

            val finalConstruct =
                "ALL RESPONSE  merchantId : $merchantID ,termianalId: $terminalID  cardNumber: $cardNumber  cardissuername: $cardIssuerNameNAddress , approvacode: $approvalCode , txnNumber: $transcationNumber ResponseText:$responseText ReferanceNo:$referanceText"

            // Log.e("paymentcar", finalConstruct)
            // storeLogToFirebase("LogResponseData", finalConstruct,"finalConstructRequest")


            return true
        } catch (ex: Exception) {
            ex.printStackTrace()
            return false
        }
        return false
    }

}