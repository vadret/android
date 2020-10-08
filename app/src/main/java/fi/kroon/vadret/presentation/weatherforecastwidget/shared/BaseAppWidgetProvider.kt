package fi.kroon.vadret.presentation.weatherforecastwidget.shared

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.os.SystemClock
import fi.kroon.vadret.util.Scheduler
import javax.inject.Inject

abstract class BaseAppWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var scheduler: Scheduler

    @Inject
    lateinit var appWidgetManager: AppWidgetManager

    val initialTriggerAtMillis: Long by lazy(LazyThreadSafetyMode.NONE) {
        SystemClock.elapsedRealtime() + 10_000L
    }
}