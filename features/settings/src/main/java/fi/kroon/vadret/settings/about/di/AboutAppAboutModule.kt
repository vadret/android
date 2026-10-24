package fi.kroon.vadret.settings.about.di

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.core.R
import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import fi.kroon.vadret.settings.about.AboutAppAboutView
import kotlinx.coroutines.flow.MutableSharedFlow

@Module
object AboutAppAboutModule {

    @Provides
    @AboutAppAboutScope
    fun provideViewState(): MutableSharedFlow<AboutAppAboutView.State> = MutableSharedFlow()

    @Provides
    @AboutAppAboutScope
    fun provideState(): AboutAppAboutView.State = AboutAppAboutView.State()

    @Provides
    @AboutAppAboutScope
    fun provideAboutInfoList(): List<AboutInfo> =
        listOf(
            AboutInfo(
                iconResourceId = R.drawable.ic_info_outline,
                titleResourceId = R.string.version_row_title,
                hintResourceId = R.string.app_version,
                urlResourceId = R.string.app_release_page
            ),
            AboutInfo(
                iconResourceId = R.drawable.ic_copyright,
                titleResourceId = R.string.license_row_title,
                hintResourceId = R.string.license_row_hint,
                urlResourceId = R.string.app_license_url
            ),
            AboutInfo(
                iconResourceId = R.drawable.ic_history,
                titleResourceId = R.string.changelog_row_title,
                urlResourceId = R.string.app_github_changelog_url
            ),
            AboutInfo(
                iconResourceId = R.drawable.ic_code,
                titleResourceId = R.string.source_code_row_title,
                urlResourceId = R.string.app_github_source_url
            )
        )
}