package fi.kroon.vadret.data.weatherforecastwidget.local

import com.afollestad.rxkprefs.RxkPrefs
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.weatherforecast.exception.WeatherForecastFailure
import fi.kroon.vadret.utils.AUTOMATIC_LOCATION_MODE_WIDGET_KEY
import fi.kroon.vadret.utils.COUNTY_WIDGET_KEY
import fi.kroon.vadret.utils.FORECAST_FORMAT_WIDGET_KEY
import fi.kroon.vadret.utils.INITIALISED_STATUS_WIDGET_KEY
import fi.kroon.vadret.utils.LAST_CHECKED_WIDGET_KEY
import fi.kroon.vadret.utils.LATITUDE_WIDGET_KEY
import fi.kroon.vadret.utils.LOCALITY_WIDGET_KEY
import fi.kroon.vadret.utils.LONGITUDE_WIDGET_KEY
import fi.kroon.vadret.utils.MUNICIPALITY_WIDGET_KEY
import fi.kroon.vadret.utils.THEME_MODE_WIDGET_KEY
import fi.kroon.vadret.utils.UPDATE_INTERVAL_WIDGET_KEY
import fi.kroon.vadret.utils.extensions.asLeft
import fi.kroon.vadret.utils.extensions.asRight
import fi.kroon.vadret.utils.extensions.asSingle
import io.reactivex.Single
import javax.inject.Inject

class WeatherForecastWidgetLocalKeyValueDataSource @Inject constructor(
    private val prefs: RxkPrefs
) {

    fun putInt(key: String, appWidgetId: Int, value: Int): Single<Either<Failure, Unit>> =
        when (key + appWidgetId) {
            (FORECAST_FORMAT_WIDGET_KEY + appWidgetId) -> {
                prefs
                    .integer(key = FORECAST_FORMAT_WIDGET_KEY + appWidgetId)
                    .set(value = value)
                    .asRight()
                    .asSingle()
            }
            else -> {
                WeatherForecastFailure
                    .LoadingWeatherSettingFailed
                    .asLeft()
                    .asSingle()
            }
        }

    fun getInt(key: String, appWidgetId: Int): Single<Either<Failure, Int>> =
        when (key + appWidgetId) {
            (FORECAST_FORMAT_WIDGET_KEY + appWidgetId) -> {
                prefs
                    .integer(key = FORECAST_FORMAT_WIDGET_KEY + appWidgetId)
                    .get()
                    .asRight()
                    .asSingle()
            }
            else -> {
                WeatherForecastFailure
                    .LoadingWeatherSettingFailed
                    .asLeft()
                    .asSingle()
            }
        }

    fun putLong(key: String, appWidgetId: Int, value: Long): Single<Either<Failure, Unit>> =
        when (key + appWidgetId) {
            (LAST_CHECKED_WIDGET_KEY + appWidgetId) -> {
                prefs.long(key = LAST_CHECKED_WIDGET_KEY + appWidgetId)
                    .set(value = value)
                    .asRight()
                    .asSingle()
            }
            else -> {
                WeatherForecastFailure
                    .LoadingWeatherSettingFailed
                    .asLeft()
                    .asSingle()
            }
        }

    fun getLong(key: String, appWidgetId: Int): Single<Either<Failure, Long>> =
        when (key + appWidgetId) {
            (LAST_CHECKED_WIDGET_KEY + appWidgetId) -> {
                prefs.long(key = LAST_CHECKED_WIDGET_KEY + appWidgetId, defaultValue = System.currentTimeMillis())
                    .get()
                    .asRight()
                    .asSingle()
            }
            else -> {
                WeatherForecastFailure
                    .LoadingWeatherSettingFailed
                    .asLeft()
                    .asSingle()
            }
        }

    fun putBoolean(key: String, appWidgetId: Int, value: Boolean): Single<Either<Failure, Unit>> =
        when (key + appWidgetId) {
            (INITIALISED_STATUS_WIDGET_KEY + appWidgetId) -> {
                prefs.boolean(key = INITIALISED_STATUS_WIDGET_KEY + appWidgetId)
                    .set(value = value)
                    .asRight()
                    .asSingle()
            }
            (AUTOMATIC_LOCATION_MODE_WIDGET_KEY + appWidgetId) -> {
                prefs.boolean(key = AUTOMATIC_LOCATION_MODE_WIDGET_KEY + appWidgetId, defaultValue = false)
                    .set(value = value)
                    .asRight()
                    .asSingle()
            }
            else -> {
                WeatherForecastFailure
                    .LoadingWeatherSettingFailed
                    .asLeft()
                    .asSingle()
            }
        }

    fun getBoolean(key: String, appWidgetId: Int): Single<Either<Failure, Boolean>> =
        when (key + appWidgetId) {
            (INITIALISED_STATUS_WIDGET_KEY + appWidgetId) -> {
                prefs.boolean(key = INITIALISED_STATUS_WIDGET_KEY + appWidgetId, defaultValue = false)
                    .get()
                    .asRight()
                    .asSingle()
            }
            (AUTOMATIC_LOCATION_MODE_WIDGET_KEY + appWidgetId) -> {
                prefs.boolean(key = AUTOMATIC_LOCATION_MODE_WIDGET_KEY + appWidgetId, defaultValue = false)
                    .get()
                    .asRight()
                    .asSingle()
            }
            else -> {
                WeatherForecastFailure
                    .LoadingWeatherSettingFailed
                    .asLeft()
                    .asSingle()
            }
        }

    fun putString(key: String, appWidgetId: Int, value: String): Single<Either<Failure, Unit>> =
        when (key + appWidgetId) {
            (UPDATE_INTERVAL_WIDGET_KEY + appWidgetId) -> {
                prefs.string(key = UPDATE_INTERVAL_WIDGET_KEY + appWidgetId)
                    .set(value = value)
                    .asRight()
                    .asSingle()
            }
            (THEME_MODE_WIDGET_KEY + appWidgetId) -> {
                prefs.string(key = THEME_MODE_WIDGET_KEY + appWidgetId)
                    .set(value = value)
                    .asRight()
                    .asSingle()
            }
            (COUNTY_WIDGET_KEY + appWidgetId) -> {
                prefs.string(key = COUNTY_WIDGET_KEY + appWidgetId)
                    .set(value = value)
                    .asRight()
                    .asSingle()
            }
            (MUNICIPALITY_WIDGET_KEY + appWidgetId) -> {
                prefs.string(key = MUNICIPALITY_WIDGET_KEY + appWidgetId)
                    .set(value = value)
                    .asRight()
                    .asSingle()
            }
            (LATITUDE_WIDGET_KEY + appWidgetId) -> {
                prefs.string(key = LATITUDE_WIDGET_KEY + appWidgetId)
                    .set(value = value)
                    .asRight()
                    .asSingle()
            }
            (LONGITUDE_WIDGET_KEY + appWidgetId) -> {
                prefs.string(key = LONGITUDE_WIDGET_KEY + appWidgetId)
                    .set(value = value)
                    .asRight()
                    .asSingle()
            }
            (LOCALITY_WIDGET_KEY + appWidgetId) -> {
                prefs.string(key = LOCALITY_WIDGET_KEY + appWidgetId)
                    .set(value = value)
                    .asRight()
                    .asSingle()
            }
            (AUTOMATIC_LOCATION_MODE_WIDGET_KEY + appWidgetId) -> {
                prefs.string(key = AUTOMATIC_LOCATION_MODE_WIDGET_KEY + appWidgetId)
                    .set(value = value)
                    .asRight()
                    .asSingle()
            }
            else -> {
                WeatherForecastFailure
                    .LoadingWeatherSettingFailed
                    .asLeft()
                    .asSingle()
            }
        }

    fun getString(key: String, appWidgetId: Int): Single<Either<Failure, String>> =
        when (key + appWidgetId) {
            (UPDATE_INTERVAL_WIDGET_KEY + appWidgetId) -> {
                prefs.string(key = UPDATE_INTERVAL_WIDGET_KEY + appWidgetId)
                    .get()
                    .asRight()
                    .asSingle()
            }
            (THEME_MODE_WIDGET_KEY + appWidgetId) -> {
                prefs.string(key = THEME_MODE_WIDGET_KEY + appWidgetId)
                    .get()
                    .asRight()
                    .asSingle()
            }
            (COUNTY_WIDGET_KEY + appWidgetId) -> {
                prefs.string(key = COUNTY_WIDGET_KEY + appWidgetId)
                    .get()
                    .asRight()
                    .asSingle()
            }
            (MUNICIPALITY_WIDGET_KEY + appWidgetId) -> {
                prefs.string(key = MUNICIPALITY_WIDGET_KEY + appWidgetId)
                    .get()
                    .asRight()
                    .asSingle()
            }
            (LATITUDE_WIDGET_KEY + appWidgetId) -> {
                prefs.string(key = LATITUDE_WIDGET_KEY + appWidgetId)
                    .get()
                    .asRight()
                    .asSingle()
            }
            (LONGITUDE_WIDGET_KEY + appWidgetId) -> {
                prefs.string(key = LONGITUDE_WIDGET_KEY + appWidgetId)
                    .get()
                    .asRight()
                    .asSingle()
            }
            (LOCALITY_WIDGET_KEY + appWidgetId) -> {
                prefs.string(key = LOCALITY_WIDGET_KEY + appWidgetId)
                    .get()
                    .asRight()
                    .asSingle()
            }
            (AUTOMATIC_LOCATION_MODE_WIDGET_KEY + appWidgetId) -> {
                prefs.string(key = AUTOMATIC_LOCATION_MODE_WIDGET_KEY + appWidgetId)
                    .get()
                    .asRight()
                    .asSingle()
            }
            else -> {
                WeatherForecastFailure
                    .LoadingWeatherSettingFailed
                    .asLeft()
                    .asSingle()
            }
        }
}