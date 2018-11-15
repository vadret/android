package fi.kroon.vadret

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.leakcanary.LeakCanary
import fi.kroon.vadret.di.component.DaggerVadretApplicationComponent
import fi.kroon.vadret.di.component.VadretApplicationComponent
import fi.kroon.vadret.di.modules.ApplicationModule
import timber.log.Timber

abstract class BaseApplication : Application() {

    val cmp: VadretApplicationComponent by lazy {
        DaggerVadretApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        cmp.inject(this)
        plantTimber()
        initThreeTenAbp()
        initLeakCanary()
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