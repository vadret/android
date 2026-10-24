package fi.kroon.vadret.data.weatherforecast.local

import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.exception.IErrorHandler
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.preference.Pref
import fi.kroon.vadret.data.preference.Prefs
import fi.kroon.vadret.util.AUTOMATIC_LOCATION_MODE_KEY
import fi.kroon.vadret.util.COUNTY_KEY
import fi.kroon.vadret.util.DEFAULT_COUNTY
import fi.kroon.vadret.util.DEFAULT_LATITUDE
import fi.kroon.vadret.util.DEFAULT_LOCALITY
import fi.kroon.vadret.util.DEFAULT_LONGITUDE
import fi.kroon.vadret.util.DEFAULT_MUNICIPALITY
import fi.kroon.vadret.util.LAST_CHECKED_KEY
import fi.kroon.vadret.util.LATITUDE_KEY
import fi.kroon.vadret.util.LOCALITY_KEY
import fi.kroon.vadret.util.LONGITUDE_KEY
import fi.kroon.vadret.util.MUNICIPALITY_KEY
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import javax.inject.Inject

class WeatherForecastLocalKeyValueDataSource @Inject constructor(
    private val prefs: Prefs,
    private val errorHandler: ErrorHandler
) : IErrorHandler by errorHandler {

    private val locationName: Pref<String> = prefs.string(LOCALITY_KEY, DEFAULT_LOCALITY)
    private val municipalityName: Pref<String> = prefs.string(MUNICIPALITY_KEY, DEFAULT_MUNICIPALITY)
    private val countyName: Pref<String> = prefs.string(COUNTY_KEY, DEFAULT_COUNTY)
    private val latitude: Pref<String> = prefs.string(LATITUDE_KEY, DEFAULT_LATITUDE)
    private val longitude: Pref<String> = prefs.string(LONGITUDE_KEY, DEFAULT_LONGITUDE)
    private val automaticLocationMode: Pref<Boolean> = prefs.boolean(AUTOMATIC_LOCATION_MODE_KEY, true)

    fun getBoolean(key: String): Either<Failure, Boolean> =
        when (key) {
            AUTOMATIC_LOCATION_MODE_KEY -> {
                automaticLocationMode
                    .get()
                    .asRight()
            }
            else -> getLocalKeyValueReadError(key = key)
        }

    fun putBoolean(key: String, value: Boolean): Either<Failure, Unit> = when (key) {
        AUTOMATIC_LOCATION_MODE_KEY -> {
            automaticLocationMode
                .set(value)
            Unit.asRight()
        }
        else -> getLocalKeyValueWriteError(key = key, value = value)
    }

    fun putLong(key: String, value: Long): Either<Failure, Unit> =
        when (key) {
            LAST_CHECKED_KEY -> {
                prefs.long(key = LAST_CHECKED_KEY)
                    .set(value = value)
                    .asRight()
            }
            else -> getLocalKeyValueWriteError(key = key, value = value)
        }

    fun getLong(key: String): Either<Failure, Long> =
        when (key) {
            LAST_CHECKED_KEY -> {
                prefs.long(key = LAST_CHECKED_KEY, defaultValue = System.currentTimeMillis())
                    .get()
                    .asRight()
            }
            else -> getLocalKeyValueReadError(key = key)
        }

    fun getString(key: String): Either<Failure, String> =
        when (key) {
            COUNTY_KEY -> {
                countyName
                    .get()
                    .asRight()
            }
            LOCALITY_KEY -> {
                locationName
                    .get()
                    .asRight()
            }
            MUNICIPALITY_KEY -> {
                municipalityName
                    .get()
                    .asRight()
            }
            LATITUDE_KEY -> {
                latitude
                    .get()
                    .asRight()
            }
            LONGITUDE_KEY -> {
                longitude
                    .get()
                    .asRight()
            }
            else -> getLocalKeyValueReadError(key = key)
        }

    fun putString(key: String, value: String): Either<Failure, Unit> =
        when (key) {
            COUNTY_KEY -> {
                countyName.set(value)
                Unit.asRight()
            }
            LOCALITY_KEY -> {
                locationName.set(value)
                Unit.asRight()
            }
            LATITUDE_KEY -> {
                latitude.set(value)
                Unit.asRight()
            }
            LONGITUDE_KEY -> {
                longitude.set(value)
                Unit.asRight()
            }
            MUNICIPALITY_KEY -> {
                municipalityName.set(value)
                Unit.asRight()
            }
            else -> getLocalKeyValueWriteError(key = key, value = value)
        }
}