package fi.kroon.vadret.settings

import fi.kroon.vadret.common.Failure
import io.github.sphrak.either.Either

internal interface SetExpandOnceTask {
    suspend operator fun invoke(isExpandOnceEnabled: Boolean): Either<Failure, Unit>
}