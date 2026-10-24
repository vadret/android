package fi.kroon.vadret.settings.di

import androidx.compose.runtime.Composable
import dagger.Component
import fi.kroon.vadret.core.CoreComponent
import fi.kroon.vadret.settings.SettingsViewModel
import fi.kroon.vadret.ui.daggerViewModel

@SettingsScope
@Component(
    dependencies = [CoreComponent::class],
    modules = [SettingsModule::class]
)
interface SettingsComponent {

    fun settingsViewModel(): SettingsViewModel

    @Component.Factory
    interface Factory {
        fun create(coreComponent: CoreComponent): SettingsComponent
    }
}

@Composable
fun settingsViewModel(): SettingsViewModel =
    daggerViewModel { _, coreComponent ->
        DaggerSettingsComponent
            .factory()
            .create(coreComponent = coreComponent)
            .settingsViewModel()
    }