package com.lfsolutions.paymentslibrary.rfm

interface RFMTransType {
    companion object
    {
        const val PURCHASE = 0x02
        const val REFUND = 0x09
        const val VOID  =  0x10
        const val TIP  = 0x03
        const val TIP_COMPLETE  = 0x03
        const val PREAUTH =  0x20
        const val PREAUTH_COMPLETE =  0x21
        const val PREAUTH_CANCEL =  0x21
        const val SIGN_IN  = 0x00
    }
}