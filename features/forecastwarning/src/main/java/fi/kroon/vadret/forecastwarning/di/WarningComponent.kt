package fi.kroon.vadret.forecastwarning.di

import androidx.compose.runtime.Composable
import dagger.Component
import fi.kroon.vadret.core.CoreComponent
import fi.kroon.vadret.forecastwarning.WarningViewModel
import fi.kroon.vadret.ui.daggerViewModel

@WarningScope
@Component(
    dependencies = [
        CoreComponent::class
    ],
    modules = [
        WarningModule::class
    ]
)
internal interface WarningComponent {

    fun warningViewModel(): WarningViewModel

    @Component.Factory
    interface Factory {
        fun create(coreComponent: CoreComponent): WarningComponent
    }
}

@Composable
internal fun warningViewModel(): WarningViewModel =
    daggerViewModel { _, coreComponent: CoreComponent ->
        DaggerWarningComponent
            .factory()
            .create(coreComponent = coreComponent)
            .warningViewModel()
    }