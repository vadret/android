package fi.kroon.vadret.settings.di

import fi.kroon.vadret.settings.DefaultGetLibrariesTask
import fi.kroon.vadret.settings.DefaultGetSettingsTask
import fi.kroon.vadret.settings.DefaultSetLocationModeTask
import fi.kroon.vadret.settings.DefaultSetThemeTask
import fi.kroon.vadret.settings.GetLibrariesTask
import fi.kroon.vadret.settings.GetSettingsTask
import fi.kroon.vadret.settings.SetLocationModeTask
import fi.kroon.vadret.settings.SetThemeTask
import fi.kroon.vadret.settings.SettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val settingsModule: Module = module {
    factoryOf(::DefaultGetLibrariesTask) { bind<GetLibrariesTask>() }
    factoryOf(::DefaultGetSettingsTask) { bind<GetSettingsTask>() }
    factoryOf(::DefaultSetLocationModeTask) { bind<SetLocationModeTask>() }
    factoryOf(::DefaultSetThemeTask) { bind<SetThemeTask>() }
    factoryOf(::SettingsViewModel)
}