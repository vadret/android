package fi.kroon.vadret.presentation.aboutapp.about.di

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.presentation.aboutapp.about.AboutAppAboutView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow

@Module
@ExperimentalCoroutinesApi
object AboutAppAboutModule {

    @Provides
    @AboutAppAboutScope
    fun provideViewState(): MutableSharedFlow<AboutAppAboutView.State> = MutableSharedFlow()

    @Provides
    @AboutAppAboutScope
    fun provideState(): AboutAppAboutView.State = AboutAppAboutView.State()
}