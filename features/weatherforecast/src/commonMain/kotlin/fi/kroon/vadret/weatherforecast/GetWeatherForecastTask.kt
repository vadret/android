package fi.kroon.vadret.weatherforecast

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.weatherforecast.model.PlaceForecast
import io.github.sphrak.either.Either

internal interface GetWeatherForecastTask {

    suspend operator fun invoke(): Either<Failure, PlaceForecast>
}