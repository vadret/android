package fi.kroon.vadret.di.modules

import android.content.Context
import androidx.collection.LruCache
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.weather.model.Weather
import fi.kroon.vadret.di.scope.VadretApplicationScope
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
    fun provideLruCache(): LruCache<Long, Weather> = LruCache(MEMORY_CACHE_SIZE)
}