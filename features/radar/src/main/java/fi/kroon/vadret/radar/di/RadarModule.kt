package fi.kroon.vadret.radar.di

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.common.DiskCache
import fi.kroon.vadret.data.common.MemoryCache
import fi.kroon.vadret.data.radar.cache.RadarDiskCache
import fi.kroon.vadret.data.radar.model.Radar
import fi.kroon.vadret.util.common.DateTimeUtil
import fi.kroon.vadret.util.common.IDateTimeUtil

@Module
internal object RadarModule {

    @Provides
    @RadarScope
    fun provideRadarMemoryCache(): MemoryCache<Long, Radar> = MemoryCache()

    @Provides
    @RadarScope
    fun provideRadarDiskCache(cache: DiskCache): RadarDiskCache = RadarDiskCache(cache)

    @Provides
    @RadarScope
    fun provideDateTimeUtil(dateTimeUtil: DateTimeUtil): IDateTimeUtil = dateTimeUtil
}