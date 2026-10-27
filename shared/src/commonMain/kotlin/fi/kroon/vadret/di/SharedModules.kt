package fi.kroon.vadret.di

import fi.kroon.vadret.forecastwarning.di.warningModule
import fi.kroon.vadret.settings.di.settingsModule
import fi.kroon.vadret.weatherforecast.di.weatherForecastModule
import org.koin.core.module.Module

val sharedModules: List<Module> = listOf(
    coreModule,
    changelogModule,
    networkModule,
    repositoryModule,
    mainModule,
    settingsModule,
    warningModule,
    weatherForecastModule
)