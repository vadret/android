package fi.kroon.vadret.core

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import fi.kroon.vadret.core.module.CacheModule
import fi.kroon.vadret.core.module.ClockModule
import fi.kroon.vadret.core.module.DatabaseModule
import fi.kroon.vadret.core.module.DispatcherModule
import fi.kroon.vadret.core.module.NetworkModule
import fi.kroon.vadret.core.module.PrefsModule
import fi.kroon.vadret.data.common.DiskCache
import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.persistance.AppDatabase
import fi.kroon.vadret.data.preference.Prefs
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.time.Clock

@CoreScope
@Component(
    modules = [
        CacheModule::class,
        ClockModule::class,
        DatabaseModule::class,
        DispatcherModule::class,
        NetworkModule::class,
        PrefsModule::class
    ]
)
interface CoreComponent {

    fun provideContext(): Context
    fun provideHttpClient(): HttpClient
    fun provideAppDatabase(): AppDatabase
    fun provideDiskCache(): DiskCache
    fun providePrefs(): Prefs
    fun provideErrorHandler(): ErrorHandler
    fun provideCoroutineDispatcher(): CoroutineDispatcher
    fun provideClock(): Clock

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            context: Context
        ): CoreComponent
    }
}