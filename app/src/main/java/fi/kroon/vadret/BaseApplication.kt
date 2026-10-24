package fi.kroon.vadret

import android.app.Application
import fi.kroon.vadret.di.AppComponent
import fi.kroon.vadret.di.DaggerAppComponent
import fi.kroon.vadret.logging.LogLevel
import fi.kroon.vadret.logging.LogcatSink
import fi.kroon.vadret.logging.LoggerFactory

private const val LOGCAT_SINK_NAME = "logcat"

abstract class BaseApplication : Application() {

    internal val appComponent: AppComponent by lazy {
        DaggerAppComponent
            .factory()
            .create(context = this)
    }

    /**
     *  Release builds log nothing.
     */
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