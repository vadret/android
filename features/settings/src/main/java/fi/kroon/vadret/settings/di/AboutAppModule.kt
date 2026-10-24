package fi.kroon.vadret.settings.di

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.settings.AboutAppView
import fi.kroon.vadret.settings.about.di.AboutAppAboutScope
import kotlinx.coroutines.flow.MutableSharedFlow

@Module
object AboutAppModule {

    @Provides
    @AboutAppAboutScope
    fun provideViewState(): MutableSharedFlow<AboutAppView.State> = MutableSharedFlow()

    @Provides
    @AboutAppAboutScope
    fun provideState(): AboutAppView.State = AboutAppView.State()
}