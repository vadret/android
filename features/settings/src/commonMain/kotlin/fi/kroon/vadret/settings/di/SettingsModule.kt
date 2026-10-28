package fi.kroon.vadret.settings.di

import fi.kroon.vadret.settings.DefaultGetChangelogTask
import fi.kroon.vadret.settings.DefaultGetLibrariesTask
import fi.kroon.vadret.settings.DefaultGetSettingsTask
import fi.kroon.vadret.settings.DefaultSetExpandOnceTask
import fi.kroon.vadret.settings.DefaultSetHourlyForecastTask
import fi.kroon.vadret.settings.DefaultSetLanguageTask
import fi.kroon.vadret.settings.DefaultSetLocationModeTask
import fi.kroon.vadret.settings.DefaultSetThemeTask
import fi.kroon.vadret.settings.GetChangelogTask
import fi.kroon.vadret.settings.GetLibrariesTask
import fi.kroon.vadret.settings.GetSettingsTask
import fi.kroon.vadret.settings.SetExpandOnceTask
import fi.kroon.vadret.settings.SetHourlyForecastTask
import fi.kroon.vadret.settings.SetLanguageTask
import fi.kroon.vadret.settings.SetLocationModeTask
import fi.kroon.vadret.settings.SetThemeTask
import fi.kroon.vadret.settings.SettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val settingsModule: Module = module {
    factoryOf(::DefaultGetChangelogTask) { bind<GetChangelogTask>() }
    factoryOf(::DefaultGetLibrariesTask) { bind<GetLibrariesTask>() }
    factoryOf(::DefaultGetSettingsTask) { bind<GetSettingsTask>() }
    factoryOf(::DefaultSetExpandOnceTask) { bind<SetExpandOnceTask>() }
    factoryOf(::DefaultSetHourlyForecastTask) { bind<SetHourlyForecastTask>() }
    factoryOf(::DefaultSetLanguageTask) { bind<SetLanguageTask>() }
    factoryOf(::DefaultSetLocationModeTask) { bind<SetLocationModeTask>() }
    factoryOf(::DefaultSetThemeTask) { bind<SetThemeTask>() }
    factoryOf(::SettingsViewModel)
}