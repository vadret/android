package fi.kroon.vadret.data.weatherforecast.local

import com.afollestad.rxkprefs.Pref
import com.afollestad.rxkprefs.RxkPrefs
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.weatherforecast.exception.WeatherForecastFailure
import fi.kroon.vadret.utils.AUTOMATIC_LOCATION_MODE_KEY
import fi.kroon.vadret.utils.COUNTY_KEY
import fi.kroon.vadret.utils.DEFAULT_COUNTY
import fi.kroon.vadret.utils.DEFAULT_LATITUDE
import fi.kroon.vadret.utils.DEFAULT_LOCALITY
import fi.kroon.vadret.utils.DEFAULT_LONGITUDE
import fi.kroon.vadret.utils.DEFAULT_MUNICIPALITY
import fi.kroon.vadret.utils.LATITUDE_KEY
import fi.kroon.vadret.utils.LOCALITY_KEY
import fi.kroon.vadret.utils.LONGITUDE_KEY
import fi.kroon.vadret.utils.MUNICIPALITY_KEY
import fi.kroon.vadret.utils.extensions.asLeft
import fi.kroon.vadret.utils.extensions.asRight
import fi.kroon.vadret.utils.extensions.asSingle
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class WeatherForecastLocalKeyValueDataSource @Inject constructor(
    private val rxkPrefs: RxkPrefs
) {

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
            else -> {
                WeatherForecastFailure
                    .LoadingWeatherSettingFailed
                    .asLeft()
                    .asSingle()
            }
        }

    fun putBoolean(key: String, value: Boolean): Single<Either<Failure, Unit>> = when (key) {
        AUTOMATIC_LOCATION_MODE_KEY -> {
            automaticLocationMode
                .set(value)
            Unit.asRight()
                .asSingle()
        }
        else -> {
            WeatherForecastFailure
                .CachingWeatherForecastDataFailed
                .asLeft()
                .asSingle()
        }
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
            else -> {
                WeatherForecastFailure
                    .LoadingWeatherSettingFailed
                    .asLeft()
                    .asSingle()
            }
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
            else -> {
                WeatherForecastFailure
                    .CachingWeatherForecastDataFailed
                    .asLeft()
                    .asSingle()
            }
        }

    fun observeBoolean(key: String): Observable<Either<Failure, Boolean>> =
        when (key) {
            AUTOMATIC_LOCATION_MODE_KEY -> automaticLocationMode.observe()
            else -> {
                throw Error("key doesn't exist")
            }
        }.map { value: Boolean ->
            Either.Right(value) as Either<Failure, Boolean>
        }.onErrorReturn {
            WeatherForecastFailure
                .LoadingWeatherSettingFailed
                .asLeft()
        }

    fun observeString(key: String): Observable<Either<Failure, String>> =
        when (key) {
            COUNTY_KEY -> {
                countyName.observe()
            }
            LOCALITY_KEY -> {
                locationName.observe()
            }
            LATITUDE_KEY -> {
                latitude.observe()
            }
            LONGITUDE_KEY -> {
                longitude.observe()
            }
            MUNICIPALITY_KEY -> {
                municipalityName.observe()
            }
            else -> {
                throw Error("key doesn't exist")
            }
        }.map { value: String ->
            Either.Right(value) as Either<Failure, String>
        }.onErrorReturn {
            WeatherForecastFailure
                .LoadingWeatherSettingFailed
                .asLeft()
        }
}