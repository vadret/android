package fi.kroon.vadret.forecastpreference.extension

import fi.kroon.vadret.forecastpreference.local.ForecastPreferencesEntity
import fi.kroon.vadret.forecastpreference.model.ForecastPreferences

private const val IS_HOURLY_FORECAST_ENABLED_BY_DEFAULT = false
private const val IS_EXPAND_ONCE_ENABLED_BY_DEFAULT = true

internal fun ForecastPreferencesEntity.toForecastPreferences(): ForecastPreferences =
    ForecastPreferences(
        isHourlyForecastEnabled = isHourlyForecastEnabled ?: IS_HOURLY_FORECAST_ENABLED_BY_DEFAULT,
        isExpandOnceEnabled = isExpandOnceEnabled ?: IS_EXPAND_ONCE_ENABLED_BY_DEFAULT
    )