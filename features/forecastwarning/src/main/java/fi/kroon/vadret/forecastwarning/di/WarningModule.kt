package fi.kroon.vadret.forecastwarning.di

import dagger.Binds
import dagger.Module
import fi.kroon.vadret.forecastwarning.DefaultGetWarningFilterTask
import fi.kroon.vadret.forecastwarning.DefaultGetWarningsTask
import fi.kroon.vadret.forecastwarning.DefaultSetWarningFilterTask
import fi.kroon.vadret.forecastwarning.GetWarningFilterTask
import fi.kroon.vadret.forecastwarning.GetWarningsTask
import fi.kroon.vadret.forecastwarning.SetWarningFilterTask

@Module
internal interface WarningModule {

    @Binds
    fun bindGetWarningsTask(defaultGetWarningsTask: DefaultGetWarningsTask): GetWarningsTask

    @Binds
    fun bindGetWarningFilterTask(defaultGetWarningFilterTask: DefaultGetWarningFilterTask): GetWarningFilterTask

    @Binds
    fun bindSetWarningFilterTask(defaultSetWarningFilterTask: DefaultSetWarningFilterTask): SetWarningFilterTask
}