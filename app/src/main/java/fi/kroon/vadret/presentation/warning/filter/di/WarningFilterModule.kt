package fi.kroon.vadret.presentation.warning.filter.di

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.presentation.warning.filter.WarningFilterView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow

@Module
@ExperimentalCoroutinesApi
object WarningFilterModule {

    @Provides
    @WarningFilterScope
    fun provideState(): WarningFilterView.State =
        WarningFilterView.State()

    @Provides
    @WarningFilterScope
    fun provideSharedFlowState(): MutableSharedFlow<WarningFilterView.State> = MutableSharedFlow()
}