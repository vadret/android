package fi.kroon.vadret

import android.app.Application
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.leakcanary.LeakCanary
import fi.kroon.vadret.di.component.DaggerVadretApplicationComponent
import fi.kroon.vadret.di.component.VadretApplicationComponent
import fi.kroon.vadret.di.modules.ApplicationModule

class VadretApplication : Application() {

    val cmp: VadretApplicationComponent by lazy {
        DaggerVadretApplicationComponent
                .builder()
                .applicationModule(ApplicationModule(this))
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        cmp.inject(this)
        initThreeTenAbp()
        initLeakCanary()
    }

    companion object {
        operator fun get(context: Context): VadretApplication {
            return context.applicationContext as VadretApplication
        }
    }

    private fun initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        LeakCanary.install(this)
    }

    private fun initThreeTenAbp() {
        AndroidThreeTen.init(this)
    }
}