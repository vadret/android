package fi.kroon.vadret

import android.content.Context
import com.google.firebase.crashlytics.FirebaseCrashlytics

class VadretApplication : BaseApplication() {

    companion object {
        operator fun get(context: Context): VadretApplication {
            return context.applicationContext as VadretApplication
        }
    }

    override fun onCreate() {
        super.onCreate()
        initCrashlytics()
    }

    private fun initCrashlytics() {
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
    }
}