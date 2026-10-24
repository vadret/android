package fi.kroon.vadret.radar.di

import android.content.Context
import androidx.collection.LruCache
import coil.ImageLoader
import com.squareup.moshi.Moshi
import dagger.Lazy
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.radar.cache.RadarDiskCache
import fi.kroon.vadret.data.radar.net.RadarNetDataSource
import fi.kroon.vadret.di.qualifiers.Radar
import fi.kroon.vadret.radar.RadarView
import fi.kroon.vadret.util.MEMORY_CACHE_SIZE
import fi.kroon.vadret.util.SMHI_API_RADAR_URL
import fi.kroon.vadret.util.common.DateTimeUtil
import fi.kroon.vadret.util.common.IDateTimeUtil
import fi.kroon.vadret.util.extension.assertNoInitMainThread
import fi.kroon.vadret.util.extension.delegatingCallFactory
import okhttp3.OkHttpClient
import okhttp3.internal.cache.DiskLruCache
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

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

    @Radar
    @Provides
    @RadarScope
    fun provideRetrofitRadar(okHttpClient: Lazy<OkHttpClient>, moshi: Moshi): Retrofit {
        assertNoInitMainThread()
        return Retrofit.Builder()
            .baseUrl(SMHI_API_RADAR_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .delegatingCallFactory(okHttpClient)
            .build()
    }

    @Provides
    @RadarScope
    fun provideRadarApi(
        @Radar
        retrofit: Retrofit
    ): RadarNetDataSource =
        retrofit.create(RadarNetDataSource::class.java)

    @Provides
    @RadarScope
    fun provideRadarLruCache(): LruCache<Long, fi.kroon.vadret.data.radar.model.Radar> = LruCache(
        MEMORY_CACHE_SIZE
    )

    @Provides
    @RadarScope
    fun provideRadarDiskCache(cache: DiskLruCache): RadarDiskCache = RadarDiskCache(cache)

    @Provides
    @RadarScope
    fun provideDateTimeUtil(dateTimeUtil: DateTimeUtil): IDateTimeUtil = dateTimeUtil
}