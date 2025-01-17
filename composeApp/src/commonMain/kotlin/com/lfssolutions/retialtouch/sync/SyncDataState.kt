package com.lfssolutions.retialtouch.sync


import com.lfssolutions.retialtouch.domain.model.sync.UnSyncList


data class SyncDataState(
    val syncInProgress:Boolean=false,
    val syncingPosInvoices:Boolean=false,
    val syncProgressStatus:String="",
    val syncError:Boolean=false,
    val syncErrorInfo:String="",
    val syncCount:Int=0,
    val syncComplete:Boolean=false,
    val lastSyncTs:Int=0,
    val reSyncTime:Int=0,
    val isPrinterEnabled:Boolean=false,
    val isBackDisplayActive:Boolean=false,
    val syncerGuid: UnSyncList = UnSyncList()
    )