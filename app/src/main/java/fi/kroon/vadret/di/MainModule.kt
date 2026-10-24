package fi.kroon.vadret.di

import dagger.Binds
import dagger.Module
import fi.kroon.vadret.main.DefaultObserveThemeTask
import fi.kroon.vadret.main.ObserveThemeTask

@Module
internal interface MainModule {

    @Binds
    fun bindObserveThemeTask(defaultObserveThemeTask: DefaultObserveThemeTask): ObserveThemeTask
}