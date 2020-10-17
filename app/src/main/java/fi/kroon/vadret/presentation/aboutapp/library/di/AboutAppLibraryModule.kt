package fi.kroon.vadret.presentation.aboutapp.library.di

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.presentation.aboutapp.library.AboutAppLibraryView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow

@Module
@ExperimentalCoroutinesApi
object AboutAppLibraryModule {

    @Provides
    @AboutAppLibraryScope
    fun provideViewState(): MutableSharedFlow<AboutAppLibraryView.State> = MutableSharedFlow()

    @Provides
    @AboutAppLibraryScope
    fun provideState(): AboutAppLibraryView.State = AboutAppLibraryView.State()
}