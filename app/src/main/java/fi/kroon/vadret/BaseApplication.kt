package fi.kroon.vadret

import android.app.Application
import fi.kroon.vadret.di.appModules
import fi.kroon.vadret.logging.LogLevel
import fi.kroon.vadret.logging.LogcatSink
import fi.kroon.vadret.logging.LoggerFactory
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication

private const val LOGCAT_SINK_NAME = "logcat"

abstract class BaseApplication : Application() {

    internal val koinApplication: KoinApplication by lazy {
        koinApplication {
            modules(modules = appModules(context = this@BaseApplication))
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            LoggerFactory.register(
                name = LOGCAT_SINK_NAME,
                sink = LogcatSink(minimumLevel = LogLevel.DEBUG)
            )
        }
    }
}