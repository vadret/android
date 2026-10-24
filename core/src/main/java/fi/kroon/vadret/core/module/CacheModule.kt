package fi.kroon.vadret.core.module

import android.content.Context
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.core.CoreScope
import fi.kroon.vadret.data.common.DiskCache
import java.io.File

@Module
object CacheModule {

    private const val DISK_CACHE_DIRECTORY = "disk_cache"

    @Provides
    @CoreScope
    fun provideDiskCache(context: Context): DiskCache =
        DiskCache(File(context.cacheDir, DISK_CACHE_DIRECTORY))
}