package fi.kroon.vadret.presentation.radar.di

import android.content.Context
import androidx.collection.LruCache
import coil.ImageLoader
import com.squareup.moshi.Moshi
import dagger.Lazy
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.radar.net.RadarNetDataSource
import fi.kroon.vadret.di.qualifiers.Radar
import fi.kroon.vadret.presentation.radar.RadarView
import fi.kroon.vadret.util.MEMORY_CACHE_SIZE
import fi.kroon.vadret.util.SMHI_API_RADAR_URL
import fi.kroon.vadret.util.Scheduler
import fi.kroon.vadret.util.common.DateTimeUtil
import fi.kroon.vadret.util.common.IDateTimeUtil
import fi.kroon.vadret.util.extension.assertNoInitMainThread
import fi.kroon.vadret.util.extension.delegatingCallFactory
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
object RadarModule {

    @Provides
    @RadarScope
    fun provideOnViewInitialisedSubject(): PublishSubject<RadarView.Event.OnViewInitialised> =
        PublishSubject.create()

    @Provides
    @RadarScope
    fun provideOnPlayButtonStartedSubject(): PublishSubject<RadarView.Event.OnPlayButtonStarted> =
        PublishSubject.create()

    @Provides
    @RadarScope
    fun provideOnPlayButtonStoppedSubject(): PublishSubject<RadarView.Event.OnPlayButtonStopped> =
        PublishSubject.create()

    @Provides
    @RadarScope
    fun provideOnFailureHandledSubject(): PublishSubject<RadarView.Event.OnFailureHandled> =
        PublishSubject.create()

    @Provides
    @RadarScope
    fun provideOnRadarImageDisplayedSubject(): PublishSubject<RadarView.Event.OnRadarImageDisplayed> =
        PublishSubject.create()

    @Provides
    @RadarScope
    fun provideOnSeekBarResetSubject(): PublishSubject<RadarView.Event.OnSeekBarReset> =
        PublishSubject.create()

    @Provides
    @RadarScope
    fun provideOnSeekBarStoppedSubject(): PublishSubject<RadarView.Event.OnSeekBarStopped> =
        PublishSubject.create()

    @Provides
    @RadarScope
    fun provideOnStateParcelUpdatedSubject(): PublishSubject<RadarView.Event.OnStateParcelUpdated> =
        PublishSubject.create()

    @Provides
    @RadarScope
    fun provideOnPositionUpdatedSubject(): PublishSubject<RadarView.Event.OnPositionUpdated> =
        PublishSubject.create()

    @Provides
    @RadarScope
    fun provideOnSeekBarRestoredSubject(): PublishSubject<RadarView.Event.OnSeekBarRestored> =
        PublishSubject.create()

    @Provides
    @RadarScope
    fun provideViewState(): RadarView.State = RadarView.State()

    @Provides
    @RadarScope
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

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
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
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
    fun provideSchedulers(): Scheduler = Scheduler()

    @Provides
    @RadarScope
    fun provideRadarLruCache(): LruCache<Long, fi.kroon.vadret.data.radar.model.Radar> = LruCache(
        MEMORY_CACHE_SIZE
    )

    @Provides
    @RadarScope
    fun provideDateTimeUtil(dateTimeUtil: DateTimeUtil): IDateTimeUtil = dateTimeUtil
}