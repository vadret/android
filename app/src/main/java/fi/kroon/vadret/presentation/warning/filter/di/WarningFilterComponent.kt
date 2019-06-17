package fi.kroon.vadret.presentation.warning.filter.di

import dagger.Subcomponent
import fi.kroon.vadret.presentation.warning.filter.WarningFilterDialogFragment

@Subcomponent(
    modules = [
        WarningFilterModule::class
    ]
)
@WarningFilterScope
interface WarningFilterComponent {

    fun inject(warningFilterDialogFragment: WarningFilterDialogFragment)

    @Subcomponent.Builder
    interface Builder {
        fun warningFilterModule(module: WarningFilterModule): Builder
        fun build(): WarningFilterComponent
    }
}