package com.lfssolutions.retialtouch

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.lfsolutions.paymentslibrary.ASCAN_REQUEST_CODE
import com.lfsolutions.paymentslibrary.Payments
import com.lfsolutions.paymentslibrary.RFM_REQUEST_CODE
import com.lfsolutions.paymentslibrary.getPaymentFactory
import com.lfssolutions.retialtouch.di.androidModule
import com.lfssolutions.retialtouch.di.appModule
import com.lfssolutions.retialtouch.presentation.viewModels.SharedPosViewModel
import com.lfssolutions.retialtouch.utils.sqldb.dbModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin


class AndroidApp : Application() {
    companion object {
        lateinit var INSTANCE: AndroidApp

        fun getApplicationContext(): Context {
            return INSTANCE.applicationContext
        }
    }
    init {
        INSTANCE=this
    }
    var currentActiveActivity: Activity? = null
        private set

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        startKoin {
            androidContext(this@AndroidApp)
            androidLogger()
            modules(dbModule + appModule() + androidModule)
        }

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                currentActiveActivity = activity
            }

            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {
                currentActiveActivity = activity
            }

            override fun onActivityPaused(activity: Activity) {
                currentActiveActivity = null
            }

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {
                if (currentActiveActivity == activity) {
                    currentActiveActivity = null
                }
            }
        })
    }
}
class EntrypointActivity : ComponentActivity() {
    private val mSharedPosViewModel: SharedPosViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContent {
            App()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RFM_REQUEST_CODE) {
            val transactionAmount = getPaymentFactory(Payments.RFM).createPayment().postProcess(
                requestCode,
                resultCode,
                data,
                this
            )
            println("transactionAmount $transactionAmount")

            if (transactionAmount > 0) {
                mSharedPosViewModel.updatePaymentStatus(transactionAmount)
                //mainViewModel.resetPaymentStatus()
            }
        }else if (requestCode == ASCAN_REQUEST_CODE) {
            val transactionAmount = getPaymentFactory(Payments.ASCAN).createPayment().postProcess(
                requestCode, resultCode, data, this
            )
            println("transactionAmount $transactionAmount")
            mSharedPosViewModel.updatePaymentStatus(transactionAmount)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}