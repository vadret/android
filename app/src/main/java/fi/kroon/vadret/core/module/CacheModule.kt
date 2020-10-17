package fi.kroon.vadret.core.module

import android.content.Context
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.core.CoreScope
import fi.kroon.vadret.util.DISK_CACHE_SIZE
import okhttp3.internal.cache.DiskLruCache
import okhttp3.internal.io.FileSystem

@Module
object CacheModule {

    @Provides
    @CoreScope
    fun provideDiskLruCache(context: Context): DiskLruCache =
        DiskLruCache.create(
            FileSystem.SYSTEM,
            context.cacheDir,
            1,
            1,
            DISK_CACHE_SIZE
        )
}