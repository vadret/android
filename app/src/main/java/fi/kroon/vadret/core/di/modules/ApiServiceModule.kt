package fi.kroon.vadret.core.di.modules

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.alert.net.AlertNetDataSource
import fi.kroon.vadret.data.common.SingleToArrayAdapter
import fi.kroon.vadret.data.nominatim.net.NominatimNetDataSource
import fi.kroon.vadret.data.radar.net.RadarApi
import fi.kroon.vadret.data.weatherforecast.net.WeatherForecastNetDataSource
import fi.kroon.vadret.core.di.qualifiers.Nominatim
import fi.kroon.vadret.core.di.qualifiers.Radar
import fi.kroon.vadret.core.di.qualifiers.Weather
import fi.kroon.vadret.core.di.scope.VadretApplicationScope
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
    @VadretApplicationScope
    fun provideWeatherApi(@Weather retrofit: Retrofit) = retrofit.create(WeatherForecastNetDataSource::class.java)

    @Provides
    @JvmStatic
    @VadretApplicationScope
    fun provideRadarApi(@Radar retrofit: Retrofit) = retrofit.create(RadarApi::class.java)

    @Provides
    @JvmStatic
    @VadretApplicationScope
    fun provideAlertApi(@Weather retrofit: Retrofit) = retrofit.create(AlertNetDataSource::class.java)

    @Provides
    @JvmStatic
    @VadretApplicationScope
    fun provideNominatimApi(@Nominatim retrofit: Retrofit) = retrofit.create(NominatimNetDataSource::class.java)

    @Provides
    @JvmStatic
    @VadretApplicationScope
    fun provideMoshi(): Moshi = Moshi
        .Builder()
        .add(SingleToArrayAdapter.INSTANCE)
        .build()

    @Nominatim
    @Provides
    @JvmStatic
    @VadretApplicationScope
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
    @VadretApplicationScope
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
    @VadretApplicationScope
    fun provideRetrofitRadar(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SMHI_API_RADAR_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    }
}