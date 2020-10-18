package fi.kroon.vadret.presentation.warning.filter.di

import dagger.Subcomponent
import fi.kroon.vadret.presentation.warning.filter.WarningFilterViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Subcomponent(
    modules = [
        WarningFilterModule::class
    ]
)
@WarningFilterScope
interface WarningFilterComponent {

    fun provideWarningFilterViewModel(): WarningFilterViewModel

    @Subcomponent.Builder
    interface Builder {
        fun warningFilterModule(module: WarningFilterModule): Builder
        fun build(): WarningFilterComponent
    }
}