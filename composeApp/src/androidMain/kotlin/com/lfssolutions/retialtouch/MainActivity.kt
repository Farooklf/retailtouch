package com.lfssolutions.retialtouch

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
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
class MainActivity : ComponentActivity() {
    //private val mSharedPosViewModel:SharedPosViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}