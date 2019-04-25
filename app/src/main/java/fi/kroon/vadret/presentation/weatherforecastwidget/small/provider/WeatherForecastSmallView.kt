package fi.kroon.vadret.presentation.weatherforecastwidget.small.provider

import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import fi.kroon.vadret.presentation.weatherforecastwidget.small.provider.model.WeatherForecastSmallModel
import fi.kroon.vadret.utils.extensions.empty

object WeatherForecastSmallView {

    sealed class Event {
        class OnWidgetInitialised(val appWidgetId: Int) : Event()
        class OnBootCompleted(val appWidgetId: Int) : Event()
        object OnWidgetUpdated : Event()
    }

    data class State(
        val text: String = String.empty(),
        val forceNet: Boolean = false,
        val timeStamp: Long = System.currentTimeMillis(),
        val appWidgetId: Int = INVALID_APPWIDGET_ID,
        val renderEvent: RenderEvent = WeatherForecastSmallView.RenderEvent.None
    )

    sealed class RenderEvent {
        object None : RenderEvent()
        class RestoreAppWidget(val appWidgetId: Int, val updateIntervalMillis: Long) : RenderEvent()
        class UpdateAppWidget(val weather: WeatherForecastSmallModel) : RenderEvent()
    }
}