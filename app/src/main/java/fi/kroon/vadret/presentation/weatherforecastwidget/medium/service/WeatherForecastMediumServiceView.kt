package fi.kroon.vadret.presentation.weatherforecastwidget.medium.service

import android.appwidget.AppWidgetManager
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.service.model.WeatherForecastMediumServiceModel

object WeatherForecastMediumServiceView {

    sealed class Event {
        data class OnInitialised(val appWidgetId: Int) : Event()
    }

    data class State(
        val renderEvent: RenderEvent = RenderEvent.None,
        val appWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID,
        val forceNet: Boolean = false
    )

    sealed class RenderEvent {
        object None : RenderEvent()
        class UpdateWeatherForecastList(val weatherForecastMediumServiceModelList: List<WeatherForecastMediumServiceModel>) : RenderEvent()
    }
}