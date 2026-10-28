package fi.kroon.vadret.forecastwarning.di

import fi.kroon.vadret.forecastwarning.DefaultMarkWarningSeenTask
import fi.kroon.vadret.forecastwarning.MarkWarningSeenTask
import fi.kroon.vadret.forecastwarning.WarningDetailViewModel
import fi.kroon.vadret.forecastwarning.WarningStoryViewModel
import fi.kroon.vadret.forecastwarning.WarningViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val warningModule: Module = module {
    factoryOf(::DefaultMarkWarningSeenTask) { bind<MarkWarningSeenTask>() }
    factoryOf(::WarningViewModel)
    factoryOf(::WarningDetailViewModel)
    factoryOf(::WarningStoryViewModel)
}