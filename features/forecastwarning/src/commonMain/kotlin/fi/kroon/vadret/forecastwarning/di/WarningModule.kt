package fi.kroon.vadret.forecastwarning.di

import fi.kroon.vadret.forecastwarning.DefaultGetWarningFilterTask
import fi.kroon.vadret.forecastwarning.DefaultSetWarningFilterTask
import fi.kroon.vadret.forecastwarning.GetWarningFilterTask
import fi.kroon.vadret.forecastwarning.SetWarningFilterTask
import fi.kroon.vadret.forecastwarning.WarningViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val warningModule: Module = module {
    factoryOf(::DefaultGetWarningFilterTask) { bind<GetWarningFilterTask>() }
    factoryOf(::DefaultSetWarningFilterTask) { bind<SetWarningFilterTask>() }
    factoryOf(::WarningViewModel)
}