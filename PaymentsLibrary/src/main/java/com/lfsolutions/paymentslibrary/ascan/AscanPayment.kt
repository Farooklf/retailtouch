package com.lfsolutions.paymentslibrary.ascan

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.lfsolutions.paymentslibrary.ASCAN_REQUEST_CODE
import com.lfsolutions.paymentslibrary.Payment
import com.lfsolutions.paymentslibrary.ascan.GlobalConstants.ASCAN_CLASS_NAME
import com.lfsolutions.paymentslibrary.ascan.GlobalConstants.PACKAGE_NAME_BOC
import com.lfsolutions.paymentslibrary.ascan.GlobalConstants.PACKAGE_NAME_DBS
import com.lfsolutions.paymentslibrary.ascan.GlobalConstants.PACKAGE_NAME_OCBC
import com.lfsolutions.paymentslibrary.isPackageInstalled
import java.text.DecimalFormat

class AscanPayment : Payment {

    var packageName = PACKAGE_NAME_DBS
    val REAL_FORMATTER: DecimalFormat = DecimalFormat("0.00")

    override fun preProcess(context: Context, processorName: String): Boolean {
        packageName = if (processorName.lowercase().contains("dbs"))
            PACKAGE_NAME_DBS
        else if (processorName.lowercase().contains("ocbc"))
            PACKAGE_NAME_OCBC
        else if (processorName.lowercase().contains("boc"))
            PACKAGE_NAME_BOC
        else
            PACKAGE_NAME_DBS
        return isPackageInstalled(packageName = packageName, context = context)
    }

    override fun process(context: Context, amount: Double) {
        var bundleString = TransBuilder.getInstance().getSaleObject(amount.toString())
        try {
            Toast.makeText(context, "bundleString $bundleString", Toast.LENGTH_LONG).show()
            val intent = Intent()
            intent.setClassName(PACKAGE_NAME_DBS, ASCAN_CLASS_NAME)
            intent.putExtra("Request", bundleString)
            (context as Activity).startActivityForResult(
                intent,
                ASCAN_REQUEST_CODE
            )
        } catch (e: Exception) {
            Toast.makeText(context, "Process Failed ${e.message}", Toast.LENGTH_SHORT).show()
            Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun postProcess(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        context: Context
    ): Double {
        Log.e("TAG", "resultCode:" + resultCode)
        Log.e("TAG", "data:" + resultCode)
        Toast.makeText(context, "resultCode: $resultCode", Toast.LENGTH_LONG).show()
        var ecrResponse: TransResponse? = null
        ecrResponse = TransResponse()
        val gson = Gson()
        val transResponse = data?.getStringExtra("Response")
        if (transResponse != null) {
            ecrResponse = gson.fromJson(
                transResponse,
                TransResponse::class.java
            )
//            Toast.makeText(
//                context,
//                "ecrResponse :transaction_amount ${ecrResponse.transaction_amount}:response_code${ecrResponse.response_code}:approval_code${ecrResponse.approval_code}",
//                Toast.LENGTH_LONG
//            ).show()

            if (ecrResponse?.response_code != null) {
                if (ecrResponse.response_code!!.contentEquals("00")) {
                    Toast.makeText(context, "Payment Successful", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(context, "Payment Failed", Toast.LENGTH_LONG)
                        .show()
                }
            }
        } else {
            Toast.makeText(context, "Payment Failed $transResponse", Toast.LENGTH_LONG)
                .show()
        }
        val amount = getConvertedAmount(ecrResponse?.transaction_amount?:"")

        return (ecrResponse?.transaction_amount?.toDouble() ?: 0.0) / 100
    }

    private fun getConvertedAmount(amount: String): String {
        val l = if (amount.isNotEmpty()) {
            amount.toDouble()
        }else{
            0.00
        }
        // l = l / 100;
        return REAL_FORMATTER.format(l)
    }
}