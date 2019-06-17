package fi.kroon.vadret.presentation.weatherforecastwidget.medium.provider

import android.appwidget.AppWidgetManager
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.provider.model.WeatherForecastMediumModel
import fi.kroon.vadret.util.extension.empty

object WeatherForecastMediumView {

    sealed class Event {
        class OnWidgetInitialised(val appWidgetId: Int) : Event()
        class OnBootCompleted(val appWidgetId: Int) : Event()
        object OnWidgetUpdated : Event()
    }

    data class State(
        val text: String = String.empty(),
        val forceNet: Boolean = false,
        val stepSize: Int = 0,
        val timeStamp: Long = System.currentTimeMillis(),
        val appWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID,
        val renderEvent: RenderEvent = WeatherForecastMediumView.RenderEvent.None
    )

    sealed class RenderEvent {
        object None : RenderEvent()
        class RestoreAppWidget(val appWidgetId: Int, val updateIntervalMillis: Long) : RenderEvent()
        class UpdateAppWidget(val weather: WeatherForecastMediumModel) : RenderEvent()
    }
}