package fi.kroon.vadret.forecastpreference.model

data class ForecastPreferences(
    val isHourlyForecastEnabled: Boolean,
    val isExpandOnceEnabled: Boolean
)