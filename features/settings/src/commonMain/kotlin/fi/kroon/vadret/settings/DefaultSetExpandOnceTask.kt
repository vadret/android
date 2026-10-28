package fi.kroon.vadret.settings

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.forecastpreference.ForecastPreferenceRepository
import io.github.sphrak.either.Either

internal class DefaultSetExpandOnceTask(
    private val forecastPreferenceRepository: ForecastPreferenceRepository
) : SetExpandOnceTask {

    override suspend fun invoke(isExpandOnceEnabled: Boolean): Either<Failure, Unit> = forecastPreferenceRepository.setExpandOnceEnabled(isEnabled = isExpandOnceEnabled)
}