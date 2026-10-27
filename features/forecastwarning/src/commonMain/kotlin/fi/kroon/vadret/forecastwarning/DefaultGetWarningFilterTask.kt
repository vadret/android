package fi.kroon.vadret.forecastwarning

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.warningfilter.WarningFilterRepository
import fi.kroon.vadret.warningfilter.model.WarningFilter
import io.github.sphrak.either.Either

internal class DefaultGetWarningFilterTask(
    private val warningFilterRepository: WarningFilterRepository
) : GetWarningFilterTask {

    override suspend fun invoke(): Either<Failure, WarningFilter> = warningFilterRepository.getWarningFilter()
}