package fi.kroon.vadret.di.modules

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.BASE_API_URL
import fi.kroon.vadret.data.weather.WeatherRepository
import fi.kroon.vadret.data.weather.net.WeatherApi
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
        fun weatherApi(retrofit: Retrofit) = retrofit.create(WeatherApi::class.java)

        @Provides
        @JvmStatic
        @VadretApplicationScope
        fun moshi(): Moshi = Moshi
                .Builder()
                .build()

        @Provides
        @JvmStatic
        @VadretApplicationScope
        fun retrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
            return Retrofit.Builder()
                    .baseUrl(BASE_API_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .client(okHttpClient)
                    .build()
        }
    }

}