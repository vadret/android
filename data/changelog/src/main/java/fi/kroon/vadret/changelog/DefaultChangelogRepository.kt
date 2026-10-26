package fi.kroon.vadret.changelog

import fi.kroon.vadret.changelog.extension.toFailure
import fi.kroon.vadret.changelog.local.ChangelogLocalDataSource
import fi.kroon.vadret.changelog.model.Changelog
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.common.catch
import fi.kroon.vadret.common.either
import io.github.sphrak.either.Either

internal class DefaultChangelogRepository(
    private val changelogLocalDataSource: ChangelogLocalDataSource
) : ChangelogRepository {

    override suspend fun getChangelog(): Either<Failure, Changelog> =
        either {
            Changelog(markdown = changelogLocalDataSource.getChangelog())
        }.catch { exception: Exception -> exception.toFailure() }
}