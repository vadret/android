package fi.kroon.vadret.presentation.weatherforecastwidget.shared

import android.appwidget.AppWidgetManager
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import fi.kroon.vadret.util.Schedulers
import javax.inject.Inject

abstract class BaseAppWidgetSetup : AppCompatActivity() {

    companion object {
        const val invalidAppWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID
        const val extraAppWidgetId: String = AppWidgetManager.EXTRA_APPWIDGET_ID
    }

    abstract fun renderError(errorCode: Int)

    @Inject
    lateinit var schedulers: Schedulers

    @Inject
    lateinit var appWidgetManager: AppWidgetManager

    val initialTriggerAtMillis: Long by lazy {
        SystemClock.elapsedRealtime() + 10_000L
    }

    protected val appWidgetId: Int by lazy {
        intent
            ?.extras
            ?.getInt(extraAppWidgetId) ?: invalidAppWidgetId
    }
}