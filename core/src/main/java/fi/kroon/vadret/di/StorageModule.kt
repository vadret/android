package fi.kroon.vadret.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.common.DiskCache
import java.io.File

/**
 *  The name the old preference screen used, so stored settings carry over.
 */
private const val SHARED_PREFERENCES_NAME = "fi.kroon.vadret.settings"
private const val DISK_CACHE_DIRECTORY = "disk_cache"

@Module
object StorageModule {

    @Provides
    @AppScope
    fun provideSharedPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    @Provides
    @AppScope
    fun provideDiskCache(context: Context): DiskCache =
        DiskCache(directory = File(context.cacheDir, DISK_CACHE_DIRECTORY))
}