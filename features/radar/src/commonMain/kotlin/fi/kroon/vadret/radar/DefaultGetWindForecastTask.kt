package fi.kroon.vadret.radar

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.wind.WindRepository
import fi.kroon.vadret.wind.model.WindForecast
import io.github.sphrak.either.Either

internal class DefaultGetWindForecastTask(
    private val windRepository: WindRepository
) : GetWindForecastTask {

    override suspend fun invoke(): Either<Failure, WindForecast> = windRepository.getWindForecast()
}