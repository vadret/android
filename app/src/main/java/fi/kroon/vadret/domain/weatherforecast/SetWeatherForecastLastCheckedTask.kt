package fi.kroon.vadret.domain.weatherforecast

import fi.kroon.vadret.data.weatherforecast.local.WeatherForecastLocalKeyValueDataSource
import fi.kroon.vadret.utils.LAST_CHECKED_KEY
import javax.inject.Inject

class SetWeatherForecastLastCheckedTask @Inject constructor(
    private val local: WeatherForecastLocalKeyValueDataSource
) {
    operator fun invoke(value: Long) = local
        .putLong(key = LAST_CHECKED_KEY, value = value)
}