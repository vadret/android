package fi.kroon.vadret.presentation.warning.display.di

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.presentation.warning.display.WarningView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow

@Module
@ExperimentalCoroutinesApi
object WarningModule {

    @Provides
    @WarningScope
    fun provideViewState(): WarningView.State =
        WarningView.State()

    @Provides
    @WarningScope
    fun provideSharedFlowState(): MutableSharedFlow<WarningView.State> = MutableSharedFlow()
}