package fi.kroon.vadret.presentation.warning.display.di

import dagger.Subcomponent
import fi.kroon.vadret.presentation.warning.display.WarningFragment

@Subcomponent(
    modules = [
        WarningModule::class
    ]
)
@WarningScope
interface WarningComponent {

    fun inject(warningFragment: WarningFragment)

    @Subcomponent.Builder
    interface Builder {
        fun warningModule(module: WarningModule): Builder
        fun build(): WarningComponent
    }
}