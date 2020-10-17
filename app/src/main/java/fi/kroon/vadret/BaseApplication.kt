package fi.kroon.vadret

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import fi.kroon.vadret.core.CoreComponent
import fi.kroon.vadret.core.CoreComponentFactory
import fi.kroon.vadret.core.CoreComponentProvider
import timber.log.Timber

abstract class BaseApplication : Application(), CoreComponentProvider {

    override val coreComponent: CoreComponent
        get() = CoreComponentFactory.getInstance(applicationContext)

    override fun onCreate() {
        super.onCreate()
        plantTimber()
        initThreeTenAbp()
        cacheDir.delete()
    }

    private fun plantTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initThreeTenAbp() {
        AndroidThreeTen.init(this)
    }
}