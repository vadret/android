package fi.kroon.vadret.data.weatherforecast.local

import com.afollestad.rxkprefs.Pref
import com.afollestad.rxkprefs.RxkPrefs
import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.exception.IErrorHandler
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
import fi.kroon.vadret.util.extension.asSingle
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject

class WeatherForecastLocalKeyValueDataSource @Inject constructor(
    private val rxkPrefs: RxkPrefs,
    private val errorHandler: ErrorHandler
) : IErrorHandler by errorHandler {

    private val locationName: Pref<String> = rxkPrefs.string(LOCALITY_KEY, DEFAULT_LOCALITY)
    private val municipalityName: Pref<String> = rxkPrefs.string(MUNICIPALITY_KEY, DEFAULT_MUNICIPALITY)
    private val countyName: Pref<String> = rxkPrefs.string(COUNTY_KEY, DEFAULT_COUNTY)
    private val latitude: Pref<String> = rxkPrefs.string(LATITUDE_KEY, DEFAULT_LATITUDE)
    private val longitude: Pref<String> = rxkPrefs.string(LONGITUDE_KEY, DEFAULT_LONGITUDE)
    private val automaticLocationMode: Pref<Boolean> = rxkPrefs.boolean(AUTOMATIC_LOCATION_MODE_KEY, true)

    fun getBoolean(key: String): Single<Either<Failure, Boolean>> =
        when (key) {
            AUTOMATIC_LOCATION_MODE_KEY -> {
                automaticLocationMode
                    .get()
                    .asRight()
                    .asSingle()
            }
            else -> getLocalKeyValueReadError(key = key)
        }

    fun putBoolean(key: String, value: Boolean): Single<Either<Failure, Unit>> = when (key) {
        AUTOMATIC_LOCATION_MODE_KEY -> {
            automaticLocationMode
                .set(value)
            Unit.asRight()
                .asSingle()
        }
        else -> getLocalKeyValueWriteError(key = key, value = value)
    }

    fun putLong(key: String, value: Long): Single<Either<Failure, Unit>> =
        when (key) {
            LAST_CHECKED_KEY -> {
                rxkPrefs.long(key = LAST_CHECKED_KEY)
                    .set(value = value)
                    .asRight()
                    .asSingle()
            }
            else -> getLocalKeyValueWriteError(key = key, value = value)
        }

    fun getLong(key: String): Single<Either<Failure, Long>> =
        when (key) {
            LAST_CHECKED_KEY -> {
                rxkPrefs.long(key = LAST_CHECKED_KEY, defaultValue = System.currentTimeMillis())
                    .get()
                    .asRight()
                    .asSingle()
            }
            else -> getLocalKeyValueReadError(key = key)
        }

    fun getString(key: String): Single<Either<Failure, String>> =
        when (key) {
            COUNTY_KEY -> {
                countyName
                    .get()
                    .asRight()
                    .asSingle()
            }
            LOCALITY_KEY -> {
                locationName
                    .get()
                    .asRight()
                    .asSingle()
            }
            MUNICIPALITY_KEY -> {
                municipalityName
                    .get()
                    .asRight()
                    .asSingle()
            }
            LATITUDE_KEY -> {
                latitude
                    .get()
                    .asRight()
                    .asSingle()
            }
            LONGITUDE_KEY -> {
                longitude
                    .get()
                    .asRight()
                    .asSingle()
            }
            else -> getLocalKeyValueReadError(key = key)
        }

    fun putString(key: String, value: String): Single<Either<Failure, Unit>> =
        when (key) {
            COUNTY_KEY -> {
                countyName.set(value)
                Unit.asRight()
                    .asSingle()
            }
            LOCALITY_KEY -> {
                locationName.set(value)
                Unit.asRight()
                    .asSingle()
            }
            LATITUDE_KEY -> {
                latitude.set(value)
                Unit.asRight()
                    .asSingle()
            }
            LONGITUDE_KEY -> {
                longitude.set(value)
                Unit.asRight()
                    .asSingle()
            }
            MUNICIPALITY_KEY -> {
                municipalityName.set(value)
                Unit.asRight()
                    .asSingle()
            }
            else -> getLocalKeyValueWriteError(key = key, value = value)
        }
}