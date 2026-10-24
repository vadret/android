package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.weatherforecast.local.WeatherForecastLocalKeyValueDataSource
import fi.kroon.vadret.data.weatherforecast.model.WeatherOut
import fi.kroon.vadret.util.LATITUDE_KEY
import fi.kroon.vadret.util.LOCALITY_KEY
import fi.kroon.vadret.util.LONGITUDE_KEY
import fi.kroon.vadret.util.extension.asRight
import fi.kroon.vadret.util.extension.toCoordinate
import io.github.sphrak.either.Either
import io.github.sphrak.either.flatMap
import javax.inject.Inject

class GetLocationManualTask @Inject constructor(
    private val keyValueStore: WeatherForecastLocalKeyValueDataSource
) {

    operator fun invoke(): Either<Failure, WeatherOut> =
        keyValueStore.getString(LOCALITY_KEY).flatMap { locality ->
            keyValueStore.getString(LATITUDE_KEY).flatMap { latitude ->
                keyValueStore.getString(LONGITUDE_KEY).flatMap { longitude ->
                    WeatherOut(
                        localityName = locality,
                        latitude = latitude.toCoordinate(),
                        longitude = longitude.toCoordinate()
                    ).asRight()
                }
            }
        }
}