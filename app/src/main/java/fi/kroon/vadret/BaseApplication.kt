package fi.kroon.vadret

import android.app.Application
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.leakcanary.LeakCanary
import fi.kroon.vadret.core.di.component.DaggerCoreApplicationComponent
import fi.kroon.vadret.core.di.component.CoreApplicationComponent
import fi.kroon.vadret.core.di.modules.ContextModule
import timber.log.Timber

abstract class BaseApplication : Application() {

    companion object {
        @JvmStatic
        fun appComponent(context: Context): CoreApplicationComponent =
            (context.applicationContext as BaseApplication).cmp
    }

    val cmp: CoreApplicationComponent by lazy(LazyThreadSafetyMode.NONE) {
        DaggerCoreApplicationComponent
            .builder()
            .contextModule(ContextModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        cmp.inject(this)
        plantTimber()
        initThreeTenAbp()
        initLeakCanary()
        cacheDir.delete()
    }

    private fun plantTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initLeakCanary() {
        if (BuildConfig.DEBUG) {
            if (LeakCanary.isInAnalyzerProcess(this)) {
                Timber.d("LeakCanary is in analyzer process")
                return
            }
            LeakCanary.install(this)
        }
    }

    private fun initThreeTenAbp() {
        AndroidThreeTen.init(this)
    }
}