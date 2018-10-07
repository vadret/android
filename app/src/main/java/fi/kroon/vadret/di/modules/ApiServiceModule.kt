package fi.kroon.vadret.di.modules

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.BASE_API_URL
import fi.kroon.vadret.data.alert.AlertApi
import fi.kroon.vadret.data.alert.AlertRepository
import fi.kroon.vadret.data.API_RADAR_URL
import fi.kroon.vadret.data.radar.RadarRepository
import fi.kroon.vadret.data.radar.net.RadarApi
import fi.kroon.vadret.data.weather.WeatherRepository
import fi.kroon.vadret.data.weather.net.WeatherApi
import fi.kroon.vadret.di.qualifiers.Radar
import fi.kroon.vadret.di.qualifiers.Weather
import fi.kroon.vadret.di.scope.VadretApplicationScope
import fi.kroon.vadret.utils.NetworkHandler
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
class ApiServiceModule {

    @Module
    companion object {

        @Provides
        @JvmStatic
        @VadretApplicationScope
        fun weatherRepository(weatherApi: WeatherApi, networkHandler: NetworkHandler) = WeatherRepository(weatherApi, networkHandler)

        @Provides
        @JvmStatic
        @VadretApplicationScope
        fun weatherApi(@Weather retrofit: Retrofit) = retrofit.create(WeatherApi::class.java)

        @Provides
        @JvmStatic
        @VadretApplicationScope
        fun radarRepository(radarApi: RadarApi, networkHandler: NetworkHandler) = RadarRepository(radarApi, networkHandler)

        @Provides
        @JvmStatic
        @VadretApplicationScope
        fun radarApi(@Radar retrofit: Retrofit) = retrofit.create(RadarApi::class.java)

        @Provides
        @JvmStatic
        @VadretApplicationScope
        fun alertRepository(alertApi: AlertApi, networkHandler: NetworkHandler) = AlertRepository(alertApi, networkHandler)

        @Provides
        @JvmStatic
        @VadretApplicationScope
        fun alertApi(@Weather retrofit: Retrofit) = retrofit.create(AlertApi::class.java)

        @Provides
        @JvmStatic
        @VadretApplicationScope
        fun moshi(): Moshi = Moshi
                .Builder()
                .build()

        @Weather
        @Provides
        @JvmStatic
        @VadretApplicationScope
        fun retrofitWeather(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
            return Retrofit.Builder()
                    .baseUrl(BASE_API_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .client(okHttpClient)
                    .build()
        }

        @Radar
        @Provides
        @JvmStatic
        @VadretApplicationScope
        fun retrofitRadar(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
            return Retrofit.Builder()
                .baseUrl(API_RADAR_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .client(okHttpClient)
                .build()
        }
    }
}