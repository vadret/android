package fi.kroon.vadret.di

import fi.kroon.vadret.changelog.ChangelogRepository
import fi.kroon.vadret.shared.resources.Res
import org.koin.core.module.Module
import org.koin.dsl.module

private const val CHANGELOG_FILE = "files/changelog.md"

internal val changelogModule: Module = module {
    single<ChangelogRepository> {
        ChangelogRepository(
            readChangelog = { Res.readBytes(path = CHANGELOG_FILE).decodeToString() },
            coroutineDispatcher = get()
        )
    }
}