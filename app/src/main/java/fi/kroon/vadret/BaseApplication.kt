package fi.kroon.vadret

import android.app.Application
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
import fi.kroon.vadret.di.component.CoreApplicationComponent
import fi.kroon.vadret.di.component.DaggerCoreApplicationComponent
import fi.kroon.vadret.di.modules.ContextModule
import fi.kroon.vadret.di.modules.DatabaseModule
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
            .databaseModule(DatabaseModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        cmp.inject(this)
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