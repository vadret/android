package fi.kroon.vadret.presentation.warning.display.di

import androidx.collection.LruCache
import com.squareup.moshi.Moshi
import dagger.Lazy
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.data.aggregatedfeed.net.AggregatedFeedNetDataSource
import fi.kroon.vadret.data.district.local.DistrictDao
import fi.kroon.vadret.data.district.net.DistrictNetDataSource
import fi.kroon.vadret.data.districtpreference.local.DistrictPreferenceDao
import fi.kroon.vadret.data.feedsource.local.FeedSourceDao
import fi.kroon.vadret.data.feedsource.net.FeedSourceNetDataSource
import fi.kroon.vadret.data.feedsourcepreference.local.FeedSourcePreferenceDao
import fi.kroon.vadret.data.persistance.AppDatabase
import fi.kroon.vadret.di.qualifiers.Alert
import fi.kroon.vadret.di.qualifiers.KrisInformation
import fi.kroon.vadret.presentation.warning.display.WarningView
import fi.kroon.vadret.util.KRISINFORMATION_API_URL
import fi.kroon.vadret.util.MEMORY_CACHE_SIZE
import fi.kroon.vadret.util.SMHI_API_ALERT_URL
import fi.kroon.vadret.util.extension.assertNoInitMainThread
import fi.kroon.vadret.util.extension.delegatingCallFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

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
    fun provideDistrictViewApi(@Alert retrofit: Retrofit): DistrictNetDataSource =
        retrofit.create(DistrictNetDataSource::class.java)

    @Provides
    @WarningScope
    fun provideFeedSourceApi(@KrisInformation retrofit: Retrofit): FeedSourceNetDataSource =
        retrofit.create(FeedSourceNetDataSource::class.java)

    @Provides
    @WarningScope
    fun provideAggregatedFeedApi(@KrisInformation retrofit: Retrofit): AggregatedFeedNetDataSource =
        retrofit.create(AggregatedFeedNetDataSource::class.java)

    @Provides
    @WarningScope
    fun provideAggregatedFeedLruCache(): LruCache<Long, List<AggregatedFeed>> = LruCache(
        MEMORY_CACHE_SIZE
    )

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

    @Alert
    @Provides
    @WarningScope
    fun provideRetrofitAlert(okHttpClient: Lazy<OkHttpClient>, moshi: Moshi): Retrofit {
        assertNoInitMainThread()
        return Retrofit.Builder()
            .baseUrl(SMHI_API_ALERT_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .delegatingCallFactory(okHttpClient)
            .build()
    }

    @KrisInformation
    @Provides
    @WarningScope
    fun provideRetrofitKrisInformation(okHttpClient: Lazy<OkHttpClient>, moshi: Moshi): Retrofit {
        assertNoInitMainThread()
        return Retrofit.Builder()
            .baseUrl(KRISINFORMATION_API_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .delegatingCallFactory(okHttpClient)
            .build()
    }
}