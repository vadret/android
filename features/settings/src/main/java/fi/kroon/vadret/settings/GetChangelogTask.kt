package fi.kroon.vadret.settings

import fi.kroon.vadret.changelog.model.Changelog
import fi.kroon.vadret.common.Failure
import io.github.sphrak.either.Either

internal interface GetChangelogTask {
    suspend operator fun invoke(): Either<Failure, Changelog>
}