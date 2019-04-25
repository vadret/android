package fi.kroon.vadret.di.modules

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.alert.net.AlertNetDataSource
import fi.kroon.vadret.data.common.SingleToArrayAdapter
import fi.kroon.vadret.data.nominatim.net.NominatimNetDataSource
import fi.kroon.vadret.data.radar.net.RadarNetDataSource
import fi.kroon.vadret.data.weatherforecast.net.WeatherForecastNetDataSource
import fi.kroon.vadret.di.qualifiers.Nominatim
import fi.kroon.vadret.di.qualifiers.Radar
import fi.kroon.vadret.di.qualifiers.Weather
import fi.kroon.vadret.di.scope.CoreApplicationScope
import fi.kroon.vadret.utils.NOMINATIM_BASE_API_URL
import fi.kroon.vadret.utils.SMHI_API_FORECAST_URL
import fi.kroon.vadret.utils.SMHI_API_RADAR_URL
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
    fun provideAlertApi(@Weather retrofit: Retrofit): AlertNetDataSource =
        retrofit.create(AlertNetDataSource::class.java)

    @Provides
    @JvmStatic
    @CoreApplicationScope
    fun provideNominatimApi(@Nominatim retrofit: Retrofit): NominatimNetDataSource =
        retrofit.create(NominatimNetDataSource::class.java)

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
    fun provideRetrofitNominatim(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NOMINATIM_BASE_API_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    }

    @Weather
    @Provides
    @JvmStatic
    @CoreApplicationScope
    fun provideRetrofitWeather(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SMHI_API_FORECAST_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    }

    @Radar
    @Provides
    @JvmStatic
    @CoreApplicationScope
    fun provideRetrofitRadar(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SMHI_API_RADAR_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    }
}