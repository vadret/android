package fi.kroon.vadret.presentation.weatherforecastwidget.shared

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.os.SystemClock
import fi.kroon.vadret.util.Schedulers
import javax.inject.Inject

abstract class BaseAppWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var schedulers: Schedulers

    @Inject
    lateinit var appWidgetManager: AppWidgetManager

    val initialTriggerAtMillis: Long by lazy {
        SystemClock.elapsedRealtime() + 10_000L
    }
}