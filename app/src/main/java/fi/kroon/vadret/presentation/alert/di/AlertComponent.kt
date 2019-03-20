package fi.kroon.vadret.presentation.alert.di

import dagger.Subcomponent
import fi.kroon.vadret.presentation.alert.AlertFragment

@Subcomponent(
    modules = [
        AlertModule::class
    ]
)
@AlertFeatureScope
interface AlertComponent {

    fun inject(alertFragment: AlertFragment)

    @Subcomponent.Builder
    interface Builder {
        fun alertModule(module: AlertModule): Builder
        fun build(): AlertComponent
    }
}