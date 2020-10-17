package fi.kroon.vadret.presentation.aboutapp.di

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.presentation.aboutapp.AboutAppView
import fi.kroon.vadret.presentation.aboutapp.about.di.AboutAppAboutScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow

@Module
@ExperimentalCoroutinesApi
object AboutAppModule {

    @Provides
    @AboutAppAboutScope
    fun provideViewState(): MutableSharedFlow<AboutAppView.State> = MutableSharedFlow()

    @Provides
    @AboutAppAboutScope
    fun provideState(): AboutAppView.State = AboutAppView.State()
}