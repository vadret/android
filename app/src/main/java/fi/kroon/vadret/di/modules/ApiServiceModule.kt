package fi.kroon.vadret.di.modules

import com.squareup.moshi.Moshi
import dagger.Lazy
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.aggregatedfeed.net.AggregatedFeedNetDataSource
import fi.kroon.vadret.data.common.SingleToArrayAdapter
import fi.kroon.vadret.data.district.net.DistrictNetDataSource
import fi.kroon.vadret.data.feedsource.net.FeedSourceNetDataSource
import fi.kroon.vadret.data.nominatim.net.NominatimNetDataSource
import fi.kroon.vadret.data.radar.net.RadarNetDataSource
import fi.kroon.vadret.data.weatherforecast.net.WeatherForecastNetDataSource
import fi.kroon.vadret.di.qualifiers.Alert
import fi.kroon.vadret.di.qualifiers.KrisInformation
import fi.kroon.vadret.di.qualifiers.Nominatim
import fi.kroon.vadret.di.qualifiers.Radar
import fi.kroon.vadret.di.qualifiers.Weather
import fi.kroon.vadret.di.scope.CoreApplicationScope
import fi.kroon.vadret.util.KRISINFORMATION_API_URL
import fi.kroon.vadret.util.NOMINATIM_BASE_API_URL
import fi.kroon.vadret.util.SMHI_API_ALERT_URL
import fi.kroon.vadret.util.SMHI_API_FORECAST_URL
import fi.kroon.vadret.util.SMHI_API_RADAR_URL
import fi.kroon.vadret.util.extension.delegatingCallFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
object ApiServiceModule {

    @Provides
    @JvmStatic
    @CoreApplicationScope
    fun provideWeatherApi(@Weather retrofit: Retrofit): WeatherForecastNetDataSource =
        retrofit.create(WeatherForecastNetDataSource::class.java)

    @Provides
    @JvmStatic
    @CoreApplicationScope
    fun provideRadarApi(@Radar retrofit: Retrofit): RadarNetDataSource =
        retrofit.create(RadarNetDataSource::class.java)

    @Provides
    @JvmStatic
    @CoreApplicationScope
    fun provideNominatimApi(@Nominatim retrofit: Retrofit): NominatimNetDataSource =
        retrofit.create(NominatimNetDataSource::class.java)

    @Provides
    @JvmStatic
    @CoreApplicationScope
    fun provideDistrictViewApi(@Alert retrofit: Retrofit): DistrictNetDataSource =
        retrofit.create(DistrictNetDataSource::class.java)

    @Provides
    @JvmStatic
    @CoreApplicationScope
    fun provideFeedSourceApi(@KrisInformation retrofit: Retrofit): FeedSourceNetDataSource =
        retrofit.create(FeedSourceNetDataSource::class.java)

    @Provides
    @JvmStatic
    @CoreApplicationScope
    fun provideAggregatedFeedApi(@KrisInformation retrofit: Retrofit): AggregatedFeedNetDataSource =
        retrofit.create(AggregatedFeedNetDataSource::class.java)

    @Provides
    @JvmStatic
    @CoreApplicationScope
    fun provideMoshi(): Moshi = Moshi
        .Builder()
        .add(SingleToArrayAdapter.INSTANCE)
        .build()

    @Nominatim
    @Provides
    @JvmStatic
    @CoreApplicationScope
    fun provideRetrofitNominatim(okHttpClient: Lazy<OkHttpClient>, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(NOMINATIM_BASE_API_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .delegatingCallFactory(okHttpClient)
            .build()

    @Weather
    @Provides
    @JvmStatic
    @CoreApplicationScope
    fun provideRetrofitWeather(okHttpClient: Lazy<OkHttpClient>, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(SMHI_API_FORECAST_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .delegatingCallFactory(okHttpClient)
            .build()

    @Alert
    @Provides
    @JvmStatic
    @CoreApplicationScope
    fun provideRetrofitAlert(okHttpClient: Lazy<OkHttpClient>, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(SMHI_API_ALERT_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .delegatingCallFactory(okHttpClient)
            .build()

    @KrisInformation
    @Provides
    @JvmStatic
    @CoreApplicationScope
    fun provideRetrofitKrisInformation(okHttpClient: Lazy<OkHttpClient>, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(KRISINFORMATION_API_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .delegatingCallFactory(okHttpClient)
            .build()
    }

    @Radar
    @Provides
    @JvmStatic
    @CoreApplicationScope
    fun provideRetrofitRadar(okHttpClient: Lazy<OkHttpClient>, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(SMHI_API_RADAR_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .delegatingCallFactory(okHttpClient)
            .build()
}