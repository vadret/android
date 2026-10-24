package fi.kroon.vadret.settings.library.di

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.settings.library.AboutAppLibraryView
import kotlinx.coroutines.flow.MutableSharedFlow

@Module
object AboutAppLibraryModule {

    @Provides
    @AboutAppLibraryScope
    fun provideViewState(): MutableSharedFlow<AboutAppLibraryView.State> = MutableSharedFlow()

    @Provides
    @AboutAppLibraryScope
    fun provideState(): AboutAppLibraryView.State = AboutAppLibraryView.State()
}