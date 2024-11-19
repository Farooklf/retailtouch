package com.lfsolutions.paymentslibrary.rfm

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.lfsolutions.paymentslibrary.Payment
import com.lfsolutions.paymentslibrary.RFM_REQUEST_CODE
import com.lfsolutions.paymentslibrary.isPackageInstalled

class RFMPayment : Payment {
    private val TAG = "RFMPayment"
    private val Target_APP = "com.app.smartpay"
    private val componentName = ComponentName(Target_APP, "com.feitian.activity.readcard.InvokeActivity")

//    private val Target_APP_TEST = "com.lfsolutions.paymenttestactivity"
//    private val testcomponentName = ComponentName(Target_APP_TEST, "com.lfsolutions.paymenttestactivity.MainActivity")

    override fun preProcess(context: Context, processorName: String): Boolean {
        return isPackageInstalled(packageName = Target_APP, context = context)
    }

    override fun process(context: Context, amount: Double) {
        val intent = Intent()
        intent.setComponent(componentName)
        intent.putExtra("AppId", context.packageName)
        intent.putExtra("trans_type", RFMTransType.PURCHASE)
        val payableAmount = encodeAmount(amount)
        /*BCD format: amount:100--->1$ ,max length:12 based on ISO8583*/
        intent.putExtra("trans_amount", payableAmount)
        (context as Activity).startActivityForResult(
            intent,
            RFM_REQUEST_CODE
        )
    }
    private fun decodeAmount(encodedAmount: Long): Double {
        return encodedAmount / 100.0
    }
    private fun encodeAmount(amount: Double): Int {
        return Math.round(amount * 100.0).toInt()
    }

    override fun postProcess(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        context: Context
    ): Double {
        Log.e("TAG", "resultCode:" + resultCode)
        Log.e("TAG", "data:" + resultCode)
        var amount =0.0

        val errorCode: Int? = data?.getIntExtra("errorCode",0)
        Log.e(TAG,"errorCode:"+errorCode)

        val errorMsg: String? = data?.getStringExtra("errorMsg")
        Log.e(TAG, "errorMsg:$errorMsg")

        val dataStr = data?.extras.toString()
        if (dataStr != null) {
            Log.e(TAG, "dataStr:$dataStr")
            //resultState.value = dataStr
        }

        /*BCD format:for example, 100--->1$(real amount) ( Based on ISO 8583 standard)*/
        val transamount: Long = data?.getLongExtra("trans_amount", 0)?:0L
        Log.e(TAG, "amount:$transamount")
        amount = decodeAmount(transamount)

        val card_no: String? = data?.getStringExtra("card_no")
        Log.e(TAG,"card_no:"+card_no)

        val terminal_no: String? = data?.getStringExtra("terminal_no")
        Log.e(TAG,"terminal_no:"+terminal_no)

        val merchant_no: String? = data?.getStringExtra("merchant_no")
        Log.e(TAG,"merchant_no:"+merchant_no)

        val cardType: String? = data?.getStringExtra("cardType")
        Log.e(TAG,"cardType:"+cardType)

        val card_expire_date: String? = data?.getStringExtra("card_expire_date")
        Log.e(TAG,"card_expire_date:" + card_expire_date)

        val aid: String? = data?.getStringExtra("aid")
        Log.e(TAG,"aid:"+aid)

        val tvr: String? = data?.getStringExtra("tvr")
        Log.e(TAG,"tvr:"+tvr)

        val tsi: String? = data?.getStringExtra("tsi")
        Log.e(TAG,"tsi:"+tsi)

        val cid: Int? = data?.getIntExtra("cid",0)
        Log.e(TAG,"cid:"+cid)

        val ac: String? = data?.getStringExtra("ac")
        Log.e(TAG,"ac:"+ac)

        val response_code: String? = data?.getStringExtra("response_code")
        Log.e(TAG,"response_code:"+response_code)

        val voucher_no: String? = data?.getStringExtra("voucher_no")
        Log.e(TAG,"voucher_no:"+voucher_no)

        val batch_no: String? = data?.getStringExtra("batch_no")
        Log.e(TAG,"batch_no:"+batch_no)

        val refer_no: String? = data?.getStringExtra("refer_no")
        Log.e(TAG,"refer_no:"+refer_no)

        val auth_no: String? = data?.getStringExtra("auth_no")
        Log.e(TAG,"auth_no:"+auth_no)

        val trans_time: String? = data?.getStringExtra("trans_time")
        Log.e(TAG, "trans_time:$trans_time")


        val currency_code: Int? = data?.getIntExtra("currency_code",0)
        Log.e(TAG,"currency_code:" + currency_code)
        if (errorMsg != null) {
            Toast.makeText(context, "$errorMsg", Toast.LENGTH_SHORT).show()
        }
        if (errorCode !=0){
            amount =0.0
        }
        return amount
    }
}