package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.weatherforecast.local.WeatherForecastLocalKeyValueDataSource
import fi.kroon.vadret.util.AUTOMATIC_LOCATION_MODE_KEY
import io.github.sphrak.either.Either
import javax.inject.Inject

class GetAppLocationModeTask @Inject constructor(
    private val weatherForecastLocalKeyValueDataSource: WeatherForecastLocalKeyValueDataSource
) {
    suspend operator fun invoke(): Either<Failure, Boolean> =
        weatherForecastLocalKeyValueDataSource
            .getBoolean(AUTOMATIC_LOCATION_MODE_KEY)
}