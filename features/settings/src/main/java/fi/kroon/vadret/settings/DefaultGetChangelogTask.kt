package fi.kroon.vadret.settings

import fi.kroon.vadret.changelog.ChangelogRepository
import fi.kroon.vadret.changelog.model.Changelog
import fi.kroon.vadret.common.Failure
import io.github.sphrak.either.Either

internal class DefaultGetChangelogTask(
    private val changelogRepository: ChangelogRepository
) : GetChangelogTask {

    override suspend fun invoke(): Either<Failure, Changelog> = changelogRepository.getChangelog()
}