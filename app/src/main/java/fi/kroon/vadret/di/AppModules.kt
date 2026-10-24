package fi.kroon.vadret.di

import android.content.Context
import fi.kroon.vadret.forecastwarning.di.warningModule
import fi.kroon.vadret.radar.di.radarModule
import fi.kroon.vadret.settings.di.settingsModule
import fi.kroon.vadret.weatherforecast.di.weatherForecastModule
import org.koin.core.module.Module
import org.koin.dsl.module

internal fun appModules(context: Context): List<Module> =
    listOf(
        module { single<Context> { context } },
        coreModule,
        networkModule,
        repositoryModule,
        mainModule,
        radarModule,
        settingsModule,
        warningModule,
        weatherForecastModule
    )