package com.hashmato.retailtouch.utils.printer

import ObjectToReceiptTemplateV1
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.USB_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.NetworkOnMainThreadException
import android.os.Parcelable
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnections
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections
import com.dantsu.escposprinter.connection.tcp.TcpConnection
import com.dantsu.escposprinter.connection.usb.UsbConnection
import com.dantsu.escposprinter.connection.usb.UsbConnections
import com.dantsu.escposprinter.exceptions.EscPosConnectionException
import com.hashmato.retailtouch.AndroidApp
import com.hashmato.retailtouch.utils.PrinterType
import comhashmatoretailtouchsqldelight.Printers
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream


class Printer(val receiptWidth: Int = 1600) {
    //    var receiptBuilder: ReceiptBuilder = ReceiptBuilder(receiptWidth)
    lateinit var printer: EscPosPrinter
    var selectedBluetoothID = ""
    var selectedUSBID = ""
    var selectedIPAddress = ""


    /*
    * @param receiptBuilder - Updated receipt build along with the property like alignment , text ,
    *  image from DrawReceipt Builder
    * This an setter function from the main user application along with the DrawReceipt property's
    * */
//    fun updateReceiptBuilder(receiptBuilder: ReceiptBuilder) {
//        this.receiptBuilder = receiptBuilder
//    }


    /*Connect USB*/

    fun connectUSBDevice(
        usbConnections: UsbConnection,
        printDPI: Int = 203,
        printerWidthMM: Float,
        printerNbrCharactersPerLine: Int = 32
    ): Boolean {
        var printDPI = if (printerWidthMM == 58f) {
            160
        } else {
            190
        }
        var printerNbrCharactersPerLineNew = if (printerWidthMM == 58f) {
            32
        } else {
            48
        }
        Log.e("Print Size", printerWidthMM.toString())
        try {
            val usbConnection = usbConnections
            printer =
                EscPosPrinter(
                    usbConnection,
                    printDPI,
                    printerWidthMM,
                    printerNbrCharactersPerLineNew
                )
            return usbConnection.isConnected
        } catch (ex: NetworkOnMainThreadException) {
            Log.e("Network Error", ex.message.toString())
            return false
        } catch (ex: EscPosConnectionException) {
            Log.e("Connection Error", ex.message.toString())
            return false
        }
    }

    /*END*/

    /*
    * @param ipAddress - IP Address of the device connected via TCP Connection
    * @param port - Port number of the IP Address
    * @param printDPI - Print DPI number
    * @param printWidthMM - Print Width in MM (Millimeter)
    * @param printerNbrCharactersPerLine - Printer Nbr Characters Per Line
    * @return return the status of the connection via TCP
    *
    * This Suspend Function helps to connect device to printer via TCP Connection along with status
    * of the connection success or failure
    *
    * This an suspend function - Please handle this function when executing
    *
    * */
    suspend fun connectTcpDevice(
        ipAddress: String,
        port: Int,
        printDPI: Int = 203,
        printerWidthMM: Float,
        printerNbrCharactersPerLine: Int = 32
    ): Boolean =
        withContext(Dispatchers.IO) {
            Log.e("Print Size", printerWidthMM.toString())
            try {
                val tcpConnection = TcpConnection(ipAddress, port)
                var printDPI = if (printerWidthMM == 58f) {
                    160
                } else {
                    190
                }
                var printerNbrCharactersPerLineNew = if (printerWidthMM == 58f) {
                    32
                } else {
                    48
                }

                printer =
                    EscPosPrinter(
                        tcpConnection,
                        printDPI,
                        printerWidthMM,
                        printerNbrCharactersPerLineNew
                    )
                return@withContext tcpConnection.isConnected
            } catch (ex: NetworkOnMainThreadException) {
                Log.e("Network Error", ex.message.toString())
                return@withContext false
            } catch (ex: EscPosConnectionException) {
                Log.e("Connection Error", ex.message.toString())
                return@withContext false
            }
        }


    /*
    * Disconnect Printer
    * */
    fun disconnectPrinter() {
        printer.disconnectPrinter()
    }


    /*
    * @param printDPI - Print DPI number
    * @param printWidthMM - Print Width in MM (Millimeter)
    * @param printerNbrCharactersPerLine - Printer Nbr Characters Per Line
    * @return return the status of the connection via Bluetooth
    *
    * This Suspend Function helps to connect device to printer via First Paired Bluetooth Device
    *  Connection along with status
    * of the connection success or failure
    *
    * This an suspend function - Please handle this function when executing
    *
    * */
    suspend fun connectBluetoothDevice(
        printDPI: Int = 203,
        printerWidthMM: Float,
        printerNbrCharactersPerLine: Int = 32
    ): Boolean =
        withContext(Dispatchers.IO) {
            Log.e("Print Size", printerWidthMM.toString())
            var printerNbrCharactersPerLineNew = if (printerWidthMM == 58f) {
                32
            } else {
                48
            }
            try {
                printer = EscPosPrinter(
                    BluetoothPrintersConnections.selectFirstPaired(), printDPI,
                    printerWidthMM, printerNbrCharactersPerLineNew
                )
                return@withContext BluetoothPrintersConnections.selectFirstPaired()?.isConnected
                    ?: false
            } catch (ex: NetworkOnMainThreadException) {
                Log.e("Network Error", ex.message.toString())
                return@withContext false
            } catch (ex: EscPosConnectionException) {
                Log.e("Connection Error", ex.message.toString())
                return@withContext false
            }
        }

    /*
    * @param bluetoothConnection - Bluetooth Device to Connect
    * @param printDPI - Print DPI number
    * @param printWidthMM - Print Width in MM (Millimeter)
    * @param printerNbrCharactersPerLine - Printer Nbr Characters Per Line
    * @return return the status of the connection via Bluetooth
    *
    * This Suspend Function helps to connect device to printer via Selected Bluetooth Device
    * Connection along with status
    * of the connection success or failure
    *
    * This an suspend function - Please handle this function when executing
    *
    * */
    suspend fun connectBluetoothDevice(
        bluetoothConnection: BluetoothConnection, printDPI: Int = 160,
        printerWidthMM: Float,
        printerNbrCharactersPerLine: Int = 32
    ): Boolean =
        withContext(Dispatchers.IO) {
            var printDPI = if (printerWidthMM == 58f) {
                160
            } else {
                190
            }
            var printerNbrCharactersPerLineNew = if (printerWidthMM == 58f) {
                32
            } else {
                48
            }
            Log.e("Print Size", printerWidthMM.toString())
            try {
                printer = EscPosPrinter(
                    bluetoothConnection,
                    printDPI,
                    printerWidthMM,
                    printerNbrCharactersPerLineNew
                )
                return@withContext bluetoothConnection.isConnected
            } catch (ex: NetworkOnMainThreadException) {
                Log.e("Network Error", ex.message.toString())
                return@withContext false
            } catch (ex: EscPosConnectionException) {
                Log.e("Connection Error", ex.message.toString())
                return@withContext false
            }
        }


    /*
    * Function returns the list of pair bluetooth devices
    *
    * @return return paired device  list
    *
    * */
    private fun getBluetoothDeviceList(): Array<out BluetoothConnection>? {
        return BluetoothConnections().list
    }


    fun printReceiptNormal(textToPrint: String): EscPosPrinter? {
        Log.e("Print Size", printer.printerWidthMM.toString())
        try {
            return printer.printFormattedTextAndCut(textToPrint, 200)
        } catch (ex: Exception) {
            Log.e("Error", ex.message.toString())
        }
        return null
    }

    fun printReceiptWithOpenCashBox(textToPrint: String): EscPosPrinter? {
        Log.e("Print Size", printer.printerWidthMM.toString())
        try {
            return printer.printFormattedTextAndOpenCashBox(textToPrint, 100)
        } catch (ex: Exception) {
            Log.e("Error", ex.message.toString())
        }
        return null
    }

    /*
    * In this function will split the bitmap image into 256px height pieces - we are doing this
    * because Max Single Image Print Size in ESCPOS Library is 256PX and add it to the string
    * builder and print it via ESCPOS print Library
    * */
//    suspend fun printReceipt(): EscPosPrinter? {
//        val receiptImage = getReceiptImage()
//        val decodedString: ByteArray =
//            Base64.decode(convertBitmapToBase64(receiptImage), Base64.DEFAULT)
//        val decodedByte = BitmapFactory.decodeByteArray(
//            decodedString, 0,
//            decodedString.size
//        )
//        val width = decodedByte.width
//        val height = decodedByte.height
//
//        val textToPrint: StringBuilder = StringBuilder("")
//
//        var y = 0
//        while (y < height) {
//            val bitmap = Bitmap.createBitmap(
//                decodedByte,
//                0,
//                y,
//                width,
//                if (y + 256 >= height) height - y else 256
//            )
//            textToPrint.append(
//                """
//                [C]<img>${
//                    PrinterTextParserImg.bitmapToHexadecimalString(
//                        printer,
//                        bitmap,
//                        false
//                    )
//                }</img>
//
//                """.trimIndent()
//            )
//            y += 256
//        }
//
//        return printer.printFormattedTextAndOpenCashBox(textToPrint.toString(), 200)
//    }

    fun openCashBox() {
        println("Open cashBox")
        printer.openCashBox()
    }

    /*
    * This function will build Printer Bitmap from ReceiptBuilder
    * @return Generate Image Bitmap from the Printer Builder
    * */
//    fun getReceiptImage(): Bitmap {
//        return receiptBuilder.build()
//    }

    /*
    * @param bitmap - Bitmap Image
    * Convert the Bitmap into Base64 String
    * */
    private fun convertBitmapToBase64(bitmap: Bitmap): String? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }


    fun getUSBConnections(context: Context): Array<out UsbConnection>? {
        return UsbConnections(context).list
    }

    private fun fetchBluetoothDevices(launchActivity: Activity) {
        val bluetoothDeviceList = getBluetoothDeviceList()
        println("bluetoothDeviceList $bluetoothDeviceList")
        if (bluetoothDeviceList?.isNotEmpty() == true) {
            val builder = AlertDialog.Builder(launchActivity)
            builder.setTitle("Select an Bluetooth Device")
            val devices = ArrayList<String>()
            var selectedDevice = bluetoothDeviceList?.get(0)
            bluetoothDeviceList.let {
                it.forEach { device_ ->
                    devices.add(device_.device.name)
                }
            }
            val checkedItem = 0
            builder.setSingleChoiceItems(
                devices.toTypedArray(),
                checkedItem
            ) { dialog, which ->
                selectedDevice = bluetoothDeviceList.get(which)

            }

            builder.setPositiveButton("OK") { dialog, which ->
//                findViewById<TextView>(R.id.printername_tv).text =
//                    selectedDevice?.device?.name
                selectedBluetoothID = selectedDevice?.device?.address.toString()
                dialog.dismiss()
            }
            builder.setNegativeButton("Cancel") { dialog, which ->
                selectedBluetoothID = ""
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        } else {
            Toast.makeText(launchActivity, "No Bluetooth Devices Available", Toast.LENGTH_SHORT)
                .show()
        }
    }

    data class bluetoothDevice(
        val name: String,
        val address: String
    )

    fun bluetoothDevices(launchActivity: Activity): List<bluetoothDevice> {
        println("bluetoothDevices $launchActivity")
        return if (!isPermissionsGranted(launchActivity)) {
            println("bluetoothDevices ${false}")
            requestBlePermissions(launchActivity)
            emptyList()
        } else {
            getBluetoothDeviceList()?.map {
                bluetoothDevice(
                    it.device.name ?: "",
                    it.device.address
                )
            } ?: emptyList()
        }
    }


    private val BLE_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    @RequiresApi(Build.VERSION_CODES.S)
    private val ANDROID_12_BLE_PERMISSIONS = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private fun requestBlePermissions(launchActivity: Activity) {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ANDROID_12_BLE_PERMISSIONS
        } else {
            BLE_PERMISSIONS
        }
        ActivityCompat.requestPermissions(launchActivity, permissions, 101)
    }

    private fun isPermissionsGranted(launchActivity: Activity): Boolean {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ANDROID_12_BLE_PERMISSIONS
        } else {
            BLE_PERMISSIONS
        }
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    launchActivity,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }


     @OptIn(DelicateCoroutinesApi::class)
     fun connectPrinter(
         printers: Printers,
         printerType: PrinterType,
         textToPrint: String,
         openDrawer: Boolean=false
     ) {
         //println("connectPrinter printerType ${printerType.name} ,printers $printers, textToPrint $textToPrint")
         GlobalScope.launch(Dispatchers.IO) {
            try {
                when (printerType) {

                    PrinterType.Ethernet -> {
                        val ethernetIPAddress = printers.networkAddress?:""
                        var ipaddress = ""
                        var port = "8080"
                        if (ethernetIPAddress.contains(":")) {
                            val splited = ethernetIPAddress.split(":")
                            ipaddress = splited[0]
                            port = splited[1]
                        }
                        if (connectTcpDevice(
                                ipaddress,
                                port.toInt(),
                                printerWidthMM = printers.paperSize?.toFloat() ?: 0f
                            )
                        ) {
                            if(textToPrint.trim().isNotEmpty()){
                                if(openDrawer){
                                   printReceiptWithOpenCashBox(textToPrint = textToPrint)
                                }else{
                                    printReceiptNormal(textToPrint=textToPrint)
                                }
                            }
                            else{
                                openCashBox()
                            }

//                            printerOrdersAndReceipts(
//                                printerItem,
//                                ticketRequest,
//                                receipt,
//                                currentCartItems,
//                                departmentName,
//                                false
//                            )
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    AndroidApp.getApplicationContext(),
                                    "Unable to connect to device , please try again",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }

                    PrinterType.USB -> {
                        val connections =
                            getUSBConnections(AndroidApp.getApplicationContext())?.let { return@let it.size }
                                ?: kotlin.run { return@run 0 }
                        if (connections > 0) {
                            var usb = getUSBConnections(AndroidApp.getApplicationContext())
                            Log.e("USD Connections", usb?.size.toString())
                            var usbConnection =
                                getUSBConnections(AndroidApp.getApplicationContext())?.first() { usbConnection ->
                                    usbConnection.device?.productId.toString()
                                        .contentEquals(printers.usbId)
                                }

                            val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
                            val usbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
                                override fun onReceive(context: Context?, intent: Intent) {
                                    val action = intent.action
                                    if (ACTION_USB_PERMISSION.equals(action)) {
                                        synchronized(this) {
                                            val usbManager =
                                                AndroidApp.getApplicationContext()
                                                    .getSystemService(USB_SERVICE) as UsbManager
                                            val usbDevice: UsbDevice? =
                                                intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
                                            if (intent.getBooleanExtra(
                                                    UsbManager.EXTRA_PERMISSION_GRANTED,
                                                    false
                                                )
                                            ) {
                                                if (usbDevice != null) {
                                                    // YOUR PRINT CODE HERE
                                                    if (usbConnection != null) {

                                                        if (connectUSBDevice(
                                                                usbConnection,
                                                                printerWidthMM = printers.paperSize?.toFloat()
                                                                    ?: 0f
                                                            )
                                                        ) {
                                                            usbConnection.write(
                                                                byteArrayOf(
                                                                    0x1B,
                                                                    0x40
                                                                )
                                                            )
                                                            usbConnection.send()
                                                            if(textToPrint.trim().isNotEmpty()){
                                                                println("connectPrinter printerType ${printerType.name} ,printers $printers, textToPrint $textToPrint")
                                                                if(openDrawer){
                                                                    printReceiptWithOpenCashBox(textToPrint = textToPrint)
                                                                }else{
                                                                    printReceiptNormal(textToPrint=textToPrint)
                                                                }
                                                            }
                                                            else{
                                                                openCashBox()
                                                            }

                                                            context?.unregisterReceiver(this)
                                                        } else {

                                                            Toast.makeText(
                                                                AndroidApp.getApplicationContext(),
                                                                "Unable to connect to device , please try again",
                                                                Toast.LENGTH_LONG
                                                            ).show()

                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            val usbManager = AndroidApp.getApplicationContext()
                                .getSystemService(USB_SERVICE) as UsbManager?
                            if (usbConnection != null && usbManager != null) {
                                val permissionIntent = PendingIntent.getBroadcast(
                                    AndroidApp.getApplicationContext(),
                                    0,
                                    Intent(ACTION_USB_PERMISSION),
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0
                                )
                                val filter = IntentFilter(ACTION_USB_PERMISSION)
                                AndroidApp.getApplicationContext().registerReceiver(usbReceiver, filter)
                                usbManager.requestPermission(usbConnection.device, permissionIntent)
                            }

                        }
                    }

                    PrinterType.Bluetooth -> {
                        val bluetoothConnection =
                            getBluetoothDeviceList()?.first { bluetoothConnection ->
                                bluetoothConnection.device?.address.toString()
                                    .contentEquals(printers.bluetoothAddress)
                            }
                        if (bluetoothConnection != null) {
                            if (connectBluetoothDevice(
                                    bluetoothConnection,
                                    printerWidthMM = printers.paperSize?.toFloat() ?: 0f
                                )
                            ) {
                                if(textToPrint.trim().isNotEmpty()){
                                    println("connectPrinter printerType ${printerType.name} ,printers $printers, textToPrint $textToPrint")
                                    if(openDrawer){
                                        printReceiptWithOpenCashBox(textToPrint = textToPrint)
                                    }else{
                                        printReceiptNormal(textToPrint=textToPrint)
                                    }
                                }
                                else{
                                    openCashBox()
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        AndroidApp.getApplicationContext(),
                                        "Unable to connect to device , please try again",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                        else{
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    AndroidApp.getApplicationContext(),
                                    "Unable to connect to device , please try again",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    suspend fun applyDynamicReceiptTemplate(ticket: Any?,currencyCode:String="", template:String,printer: Printers):String{
        return ObjectToReceiptTemplateV1.processTemplate(template = template,data = ticket,currencyCode=currencyCode,printerWidth=printer.paperSize?.toFloat()?:80f)
    }
}
