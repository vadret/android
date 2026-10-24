package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.weatherforecast.model.PlaceForecast
import io.github.sphrak.either.Either

internal interface GetWeatherForecastTask {

    /**
     *  The forecast for the device location while the location is
     *  automatic, otherwise for the place stored last.
     */
    suspend operator fun invoke(): Either<Failure, PlaceForecast>
}