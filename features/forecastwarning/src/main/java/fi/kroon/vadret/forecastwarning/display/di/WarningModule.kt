package fi.kroon.vadret.forecastwarning.display.di

import androidx.collection.LruCache
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.aggregatedfeed.cache.AggregatedFeedDiskCache
import fi.kroon.vadret.data.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.data.common.DiskCache
import fi.kroon.vadret.data.district.local.DistrictDao
import fi.kroon.vadret.data.districtpreference.local.DistrictPreferenceDao
import fi.kroon.vadret.data.feedsource.local.FeedSourceDao
import fi.kroon.vadret.data.feedsourcepreference.local.FeedSourcePreferenceDao
import fi.kroon.vadret.data.persistance.AppDatabase
import fi.kroon.vadret.forecastwarning.display.WarningView
import fi.kroon.vadret.util.MEMORY_CACHE_SIZE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow

@Module
@ExperimentalCoroutinesApi
object WarningModule {

    @Provides
    @WarningScope
    fun provideViewState(): WarningView.State =
        WarningView.State()

    @Provides
    @WarningScope
    fun provideSharedFlowState(): MutableSharedFlow<WarningView.State> = MutableSharedFlow()

    @Provides
    @WarningScope
    fun provideAggregatedFeedLruCache(): LruCache<Long, List<AggregatedFeed>> = LruCache(
        MEMORY_CACHE_SIZE
    )

    @Provides
    @WarningScope
    fun provideAggregatedFeedDiskCache(cache: DiskCache): AggregatedFeedDiskCache = AggregatedFeedDiskCache(cache)

    @Provides
    @WarningScope
    fun provideDistrictDao(appDatabase: AppDatabase): DistrictDao =
        appDatabase.districtDao()

    @Provides
    @WarningScope
    fun provideFeedSourceDao(appDatabase: AppDatabase): FeedSourceDao =
        appDatabase.feedSourceDao()

    @Provides
    @WarningScope
    fun provideDistrictPreferenceDao(appDatabase: AppDatabase): DistrictPreferenceDao =
        appDatabase.districtPreferenceDao()

    @Provides
    @WarningScope
    fun provideFeedSourcePreferenceDao(appDatabase: AppDatabase): FeedSourcePreferenceDao =
        appDatabase.feedSourcePreferenceDao()
}