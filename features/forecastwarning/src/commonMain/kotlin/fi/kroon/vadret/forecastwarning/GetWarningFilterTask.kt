package fi.kroon.vadret.forecastwarning

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.warningfilter.model.WarningFilter
import io.github.sphrak.either.Either

internal interface GetWarningFilterTask {
    suspend operator fun invoke(): Either<Failure, WarningFilter>
}