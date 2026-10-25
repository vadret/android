package fi.kroon.vadret.weatherforecast.extension

import fi.kroon.vadret.weatherforecast.WeatherForecastState
import fi.kroon.vadret.weatherforecast.model.ComingDay
import fi.kroon.vadret.weatherforecast.model.WeatherForecast
import kotlinx.collections.immutable.ImmutableList

internal fun WeatherForecastState.withComingDays(comingDays: ImmutableList<ComingDay>): WeatherForecastState =
    copy(
        forecast = forecast.copy(comingDays = comingDays),
        isEveryDayExpanded = comingDays.isEveryDayExpanded()
    )

internal fun WeatherForecastState.withForecast(
    forecast: WeatherForecast,
    isSamePlace: Boolean
): WeatherForecastState {
    val comingDays: ImmutableList<ComingDay> = if (isSamePlace) {
        forecast.comingDays.withExpansionOf(previousComingDays = this.forecast.comingDays)
    } else {
        forecast.comingDays
    }
    return copy(forecast = forecast).withComingDays(comingDays = comingDays)
}