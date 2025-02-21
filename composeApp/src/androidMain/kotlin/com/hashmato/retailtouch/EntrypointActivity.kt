package com.hashmato.retailtouch

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Bundle
import android.view.Display
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.lfsolutions.paymentslibrary.ASCAN_REQUEST_CODE
import com.lfsolutions.paymentslibrary.Payments
import com.lfsolutions.paymentslibrary.RFM_REQUEST_CODE
import com.lfsolutions.paymentslibrary.getPaymentFactory
import com.hashmato.retailtouch.di.androidModule
import com.hashmato.retailtouch.di.appModule
import com.hashmato.retailtouch.presentation.viewModels.SharedPosViewModel
import com.hashmato.retailtouch.utils.ConnectivityObserver
import com.hashmato.retailtouch.utils.secondDisplay.WelcomePresentationDisplay
import com.hashmato.retailtouch.utils.sqldb.dbModule
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
        if (resources.getBoolean(R.bool.portrait_only)) {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } else {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }
        //Start observing network changes
         ConnectivityObserver.startObserving()

        if (!android.provider.Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Please provide the permission", Toast.LENGTH_SHORT).show();
            startActivity(Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
        }
        setupSecondaryDisplay()
        setContent {
            ComposeApp.RootContent()
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

    private fun setupSecondaryDisplay(){
          val mDisplayManager = getSystemService(DISPLAY_SERVICE) as DisplayManager
          val displays: Array<Display>?=mDisplayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION)
          if(displays!=null && getPresentationDisplays() != null){
              val presentationDisplays= mDisplayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION)
              if(presentationDisplays.isNotEmpty()){
                  val secondaryDisplay = WelcomePresentationDisplay(
                      this,
                      presentationDisplays[0],
                      null
                  )
                  secondaryDisplay.show()
                  ShareDisplayObject.secondaryDisplayInstance = secondaryDisplay
              }else{
                  Toast.makeText(this,"No Secondary Display",Toast.LENGTH_LONG).show()
              }
          }else {
              Toast.makeText(this,"No Display",Toast.LENGTH_LONG).show()
          }
    }

    private fun getPresentationDisplays() : Display?{
        val mDisplayManager = getSystemService(DISPLAY_SERVICE) as DisplayManager
        val displays: Array<Display>? = mDisplayManager.displays
        if (displays != null) {
            for (i in displays.indices) {
                if (displays[i].getFlags() and Display.FLAG_SECURE !== 0 && displays[i].getFlags() and Display.FLAG_SUPPORTS_PROTECTED_BUFFERS !== 0 && displays[i].getFlags() and Display.FLAG_PRESENTATION !== 0) {
                    return displays[i]
                }
            }
        }
        return null
    }
}

object ShareDisplayObject {
    var secondaryDisplayInstance: WelcomePresentationDisplay? = null

}

@Preview
@Composable
fun AppAndroidPreview() {
    ComposeApp.RootContent()
}