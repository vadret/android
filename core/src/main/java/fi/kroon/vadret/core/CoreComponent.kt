package fi.kroon.vadret.core

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import fi.kroon.vadret.core.module.CacheModule
import fi.kroon.vadret.core.module.DatabaseModule
import fi.kroon.vadret.core.module.NetworkModule
import fi.kroon.vadret.core.module.PrefsModule
import fi.kroon.vadret.data.common.DiskCache
import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.persistance.AppDatabase
import fi.kroon.vadret.data.preference.Prefs
import io.ktor.client.HttpClient

@CoreScope
@Component(
    modules = [
        CacheModule::class,
        DatabaseModule::class,
        NetworkModule::class,
        PrefsModule::class
    ]
)
interface CoreComponent {

    fun provideHttpClient(): HttpClient
    fun provideAppDatabase(): AppDatabase
    fun provideDiskCache(): DiskCache
    fun providePrefs(): Prefs
    fun provideErrorHandler(): ErrorHandler

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            context: Context
        ): CoreComponent
    }
}