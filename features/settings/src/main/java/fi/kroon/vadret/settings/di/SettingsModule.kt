package fi.kroon.vadret.settings.di

import dagger.Binds
import dagger.Module
import fi.kroon.vadret.settings.DefaultGetLibrariesTask
import fi.kroon.vadret.settings.DefaultGetSettingsTask
import fi.kroon.vadret.settings.DefaultSetLocationModeTask
import fi.kroon.vadret.settings.DefaultSetThemeTask
import fi.kroon.vadret.settings.GetLibrariesTask
import fi.kroon.vadret.settings.GetSettingsTask
import fi.kroon.vadret.settings.SetLocationModeTask
import fi.kroon.vadret.settings.SetThemeTask

@Module
internal interface SettingsModule {

    @Binds
    fun bindGetLibrariesTask(defaultGetLibrariesTask: DefaultGetLibrariesTask): GetLibrariesTask

    @Binds
    fun bindGetSettingsTask(defaultGetSettingsTask: DefaultGetSettingsTask): GetSettingsTask

    @Binds
    fun bindSetLocationModeTask(defaultSetLocationModeTask: DefaultSetLocationModeTask): SetLocationModeTask

    @Binds
    fun bindSetThemeTask(defaultSetThemeTask: DefaultSetThemeTask): SetThemeTask
}