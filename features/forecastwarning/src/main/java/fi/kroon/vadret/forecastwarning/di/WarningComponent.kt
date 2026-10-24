package fi.kroon.vadret.forecastwarning.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.Component
import fi.kroon.vadret.forecastwarning.WarningViewModel

@Component(
    dependencies = [
        WarningDependencies::class
    ],
    modules = [
        WarningModule::class
    ]
)
internal interface WarningComponent {

    fun warningViewModel(): WarningViewModel

    @Component.Factory
    interface Factory {
        fun create(warningDependencies: WarningDependencies): WarningComponent
    }
}

/**
 *  One view model per navigation entry.
 */
@Composable
internal fun warningViewModel(warningDependencies: WarningDependencies): WarningViewModel =
    viewModel {
        DaggerWarningComponent
            .factory()
            .create(warningDependencies = warningDependencies)
            .warningViewModel()
    }