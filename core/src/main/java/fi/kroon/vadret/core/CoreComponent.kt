package fi.kroon.vadret.core

import android.content.Context
import com.afollestad.rxkprefs.RxkPrefs
import com.squareup.moshi.Moshi
import dagger.BindsInstance
import dagger.Component
import fi.kroon.vadret.core.module.ApiServiceModule
import fi.kroon.vadret.core.module.CacheModule
import fi.kroon.vadret.core.module.DatabaseModule
import fi.kroon.vadret.core.module.NetworkModule
import fi.kroon.vadret.core.module.RxkPrefsModule
import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.persistance.AppDatabase
import okhttp3.OkHttpClient
import okhttp3.internal.cache.DiskLruCache

@CoreScope
@Component(
    modules = [
        ApiServiceModule::class,
        CacheModule::class,
        DatabaseModule::class,
        NetworkModule::class,
        RxkPrefsModule::class
    ]
)
interface CoreComponent {

    fun provideOkHttpClient(): OkHttpClient
    fun provideAppDatabase(): AppDatabase
    fun provideMoshi(): Moshi
    fun provideDiskLruCache(): DiskLruCache
    fun provideRxkPrefs(): RxkPrefs
    fun provideErrorHandler(): ErrorHandler

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            context: Context
        ): CoreComponent
    }
}