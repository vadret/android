package fi.kroon.vadret.settings

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.forecastpreference.ForecastPreferenceRepository
import io.github.sphrak.either.Either

internal class DefaultSetHourlyForecastTask(
    private val forecastPreferenceRepository: ForecastPreferenceRepository
) : SetHourlyForecastTask {

    override suspend fun invoke(isHourlyForecastEnabled: Boolean): Either<Failure, Unit> = forecastPreferenceRepository.setHourlyForecastEnabled(isEnabled = isHourlyForecastEnabled)
}