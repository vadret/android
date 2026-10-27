package fi.kroon.vadret.settings

import fi.kroon.vadret.common.Failure
import io.github.sphrak.either.Either

internal interface GetSettingsTask {
    suspend operator fun invoke(): Either<Failure, Settings>
}