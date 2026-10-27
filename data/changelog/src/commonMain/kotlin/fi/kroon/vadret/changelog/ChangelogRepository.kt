package fi.kroon.vadret.changelog

import fi.kroon.vadret.changelog.local.DefaultChangelogLocalDataSource
import fi.kroon.vadret.changelog.model.Changelog
import fi.kroon.vadret.common.Failure
import io.github.sphrak.either.Either
import kotlinx.coroutines.CoroutineDispatcher

interface ChangelogRepository {
    suspend fun getChangelog(): Either<Failure, Changelog>
}

fun ChangelogRepository(
    readChangelog: suspend () -> String,
    coroutineDispatcher: CoroutineDispatcher
): ChangelogRepository =
    DefaultChangelogRepository(
        changelogLocalDataSource = DefaultChangelogLocalDataSource(
            readChangelog = readChangelog,
            coroutineDispatcher = coroutineDispatcher
        )
    )