package fi.kroon.vadret.presentation.warning.display.di

import dagger.Subcomponent
import fi.kroon.vadret.presentation.warning.display.WarningAdapter
import fi.kroon.vadret.presentation.warning.display.WarningViewModel

@Subcomponent(
    modules = [
        WarningModule::class
    ]
)
@WarningScope
interface WarningComponent {

    fun provideWarningViewModel(): WarningViewModel
    fun provideWarningAdapter(): WarningAdapter

    @Subcomponent.Builder
    interface Builder {
        fun warningModule(module: WarningModule): Builder
        fun build(): WarningComponent
    }
}