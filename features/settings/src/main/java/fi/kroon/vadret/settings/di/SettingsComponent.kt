package fi.kroon.vadret.settings.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.Component
import fi.kroon.vadret.settings.SettingsViewModel

@Component(
    dependencies = [
        SettingsDependencies::class
    ],
    modules = [
        SettingsModule::class
    ]
)
internal interface SettingsComponent {

    fun settingsViewModel(): SettingsViewModel

    @Component.Factory
    interface Factory {
        fun create(settingsDependencies: SettingsDependencies): SettingsComponent
    }
}

/**
 *  One view model per navigation entry.
 */
@Composable
internal fun settingsViewModel(settingsDependencies: SettingsDependencies): SettingsViewModel =
    viewModel {
        DaggerSettingsComponent
            .factory()
            .create(settingsDependencies = settingsDependencies)
            .settingsViewModel()
    }