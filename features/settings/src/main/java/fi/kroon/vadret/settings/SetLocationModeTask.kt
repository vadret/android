package fi.kroon.vadret.settings

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.weatherforecast.local.WeatherForecastLocalKeyValueDataSource
import fi.kroon.vadret.util.AUTOMATIC_LOCATION_MODE_KEY
import io.github.sphrak.either.Either
import javax.inject.Inject

class SetLocationModeTask @Inject constructor(
    private val weatherForecastLocalKeyValueDataSource: WeatherForecastLocalKeyValueDataSource
) {

    operator fun invoke(isGpsEnabled: Boolean): Either<Failure, Unit> =
        weatherForecastLocalKeyValueDataSource.putBoolean(
            key = AUTOMATIC_LOCATION_MODE_KEY,
            value = isGpsEnabled
        )
}