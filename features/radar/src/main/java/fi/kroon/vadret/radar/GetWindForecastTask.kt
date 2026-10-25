package fi.kroon.vadret.radar

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.wind.model.WindForecast
import io.github.sphrak.either.Either

internal interface GetWindForecastTask {

    suspend operator fun invoke(): Either<Failure, WindForecast>
}