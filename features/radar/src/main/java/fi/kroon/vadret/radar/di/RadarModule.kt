package fi.kroon.vadret.radar.di

import android.content.Context
import androidx.collection.LruCache
import coil.ImageLoader
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.common.DiskCache
import fi.kroon.vadret.data.radar.cache.RadarDiskCache
import fi.kroon.vadret.radar.RadarView
import fi.kroon.vadret.util.MEMORY_CACHE_SIZE
import fi.kroon.vadret.util.common.DateTimeUtil
import fi.kroon.vadret.util.common.IDateTimeUtil

@Module
object RadarModule {

    @Provides
    @RadarScope
    fun provideViewState(): RadarView.State = RadarView.State()

    @Provides
    @RadarScope
    fun provideImageLoader(context: Context): ImageLoader =
        ImageLoader
            .Builder(context)
            .availableMemoryPercentage(0.25)
            .crossfade(true)
            .build()

    @Provides
    @RadarScope
    fun provideRadarLruCache(): LruCache<Long, fi.kroon.vadret.data.radar.model.Radar> = LruCache(
        MEMORY_CACHE_SIZE
    )

    @Provides
    @RadarScope
    fun provideRadarDiskCache(cache: DiskCache): RadarDiskCache = RadarDiskCache(cache)

    @Provides
    @RadarScope
    fun provideDateTimeUtil(dateTimeUtil: DateTimeUtil): IDateTimeUtil = dateTimeUtil
}