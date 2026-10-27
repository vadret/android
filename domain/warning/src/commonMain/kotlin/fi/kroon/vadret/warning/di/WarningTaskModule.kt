package fi.kroon.vadret.warning.di

import fi.kroon.vadret.warning.DefaultGetWarningsTask
import fi.kroon.vadret.warning.GetWarningsTask
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val warningTaskModule: Module = module {
    factoryOf(::DefaultGetWarningsTask) { bind<GetWarningsTask>() }
}