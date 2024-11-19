package com.lfsolutions.paymentslibrary

import android.content.Context
import android.content.Intent

interface Payment {

    fun preProcess(context: Context,processorName:String):Boolean
    fun process(context: Context, amount:Double)
    fun postProcess(requestCode: Int, resultCode: Int, data: Intent?, context: Context):Double
}