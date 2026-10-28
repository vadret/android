package fi.kroon.vadret.forecastpreference.local

internal data class ForecastPreferencesEntity(
    val isHourlyForecastEnabled: Boolean?,
    val isExpandOnceEnabled: Boolean?
)