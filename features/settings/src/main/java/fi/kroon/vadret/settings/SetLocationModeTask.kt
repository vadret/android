package fi.kroon.vadret.settings

import fi.kroon.vadret.common.Failure
import io.github.sphrak.either.Either

internal interface SetLocationModeTask {
    suspend operator fun invoke(isAutomaticLocation: Boolean): Either<Failure, Unit>
}