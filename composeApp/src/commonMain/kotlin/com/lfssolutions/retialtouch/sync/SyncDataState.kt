package com.lfssolutions.retialtouch.sync


import com.lfssolutions.retialtouch.domain.model.sync.SyncResult


data class SyncDataState(
    val syncInProgress:Boolean=false,
    val syncProgressStatus:String="",
    val syncError:Boolean=false,
    val syncErrorInfo:String="",
    val syncCount:Int=0,
    val lastSyncTs:Int=0,
    val reSyncTime:Int=0,
    val isPrinterEnabled:Boolean=false,
    val isBackDisplayActive:Boolean=false,
    val syncerGuid: SyncResult = SyncResult()
    )