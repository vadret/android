package fi.kroon.vadret.di

import android.content.Context
import fi.kroon.vadret.R
import fi.kroon.vadret.changelog.ChangelogRepository
import org.koin.core.module.Module
import org.koin.dsl.module

internal val changelogModule: Module = module {
    single<ChangelogRepository> {
        val context: Context = get()
        ChangelogRepository(
            openChangelog = { context.resources.openRawResource(R.raw.changelog) },
            coroutineDispatcher = get()
        )
    }
}