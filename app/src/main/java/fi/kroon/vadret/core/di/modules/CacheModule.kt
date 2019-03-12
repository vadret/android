package fi.kroon.vadret.core.di.modules

import android.content.Context
import androidx.collection.LruCache
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.weatherforecast.model.Weather
import fi.kroon.vadret.core.di.scope.VadretApplicationScope
import fi.kroon.vadret.data.alert.model.Alert
import fi.kroon.vadret.utils.DISK_CACHE_SIZE
import fi.kroon.vadret.utils.MEMORY_CACHE_SIZE
import okhttp3.internal.cache.DiskLruCache
import okhttp3.internal.io.FileSystem

@Module
object CacheModule {

    @Provides
    @JvmStatic
    @VadretApplicationScope
    fun provideDiskLruCache(context: Context): DiskLruCache =
        DiskLruCache.create(
            FileSystem.SYSTEM,
            context.cacheDir,
            1,
            1,
            DISK_CACHE_SIZE
        )

    @Provides
    @JvmStatic
    @VadretApplicationScope
    fun provideWeatherLruCache(): LruCache<Long, Weather> = LruCache(MEMORY_CACHE_SIZE)

    @Provides
    @JvmStatic
    @VadretApplicationScope
    fun provideAlertLruCache(): LruCache<Long, Alert> = LruCache(MEMORY_CACHE_SIZE)
}