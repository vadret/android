package fi.kroon.vadret.presentation.weatherforecast.di

import android.content.Context
import android.location.LocationManager
import androidx.collection.LruCache
import com.squareup.moshi.Moshi
import dagger.Lazy
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.location.local.LocationLocalDataSource
import fi.kroon.vadret.data.nominatim.net.NominatimNetDataSource
import fi.kroon.vadret.data.weatherforecast.model.Weather
import fi.kroon.vadret.data.weatherforecast.net.WeatherForecastNetDataSource
import fi.kroon.vadret.di.qualifiers.Nominatim
import fi.kroon.vadret.di.qualifiers.WeatherQualifier
import fi.kroon.vadret.presentation.weatherforecast.WeatherForecastView
import fi.kroon.vadret.util.MEMORY_CACHE_SIZE
import fi.kroon.vadret.util.NOMINATIM_BASE_API_URL
import fi.kroon.vadret.util.SMHI_API_FORECAST_URL
import fi.kroon.vadret.util.Scheduler
import fi.kroon.vadret.util.extension.assertNoInitMainThread
import fi.kroon.vadret.util.extension.delegatingCallFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
@ExperimentalCoroutinesApi
object WeatherForecastModule {

    @Provides
    @WeatherForecastScope
    fun provideEventChannel(): ConflatedBroadcastChannel<WeatherForecastView.Event> =
        ConflatedBroadcastChannel()

    @Provides
    @WeatherForecastScope
    fun provideViewState(): WeatherForecastView.State = WeatherForecastView.State()

    @Provides
    @WeatherForecastScope
    fun provideSchedulers(): Scheduler = Scheduler()

    @WeatherQualifier
    @Provides
    @WeatherForecastScope
    fun provideRetrofitWeather(okHttpClient: Lazy<OkHttpClient>, moshi: Moshi): Retrofit {
        assertNoInitMainThread()
        return Retrofit.Builder()
            .baseUrl(SMHI_API_FORECAST_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .delegatingCallFactory(okHttpClient)
            .build()
    }

    @Nominatim
    @Provides
    @WeatherForecastScope
    fun provideRetrofitNominatim(okHttpClient: Lazy<OkHttpClient>, moshi: Moshi): Retrofit {
        assertNoInitMainThread()
        return Retrofit.Builder()
            .baseUrl(NOMINATIM_BASE_API_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .delegatingCallFactory(okHttpClient)
            .build()
    }

    @Provides
    @WeatherForecastScope
    fun provideWeatherApi(@WeatherQualifier retrofit: Retrofit): WeatherForecastNetDataSource =
        retrofit.create(WeatherForecastNetDataSource::class.java)

    @Provides
    @WeatherForecastScope
    fun provideNominatimApi(@Nominatim retrofit: Retrofit): NominatimNetDataSource =
        retrofit.create(NominatimNetDataSource::class.java)

    @Provides
    @WeatherForecastScope
    fun provideLocationManager(context: Context): LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @Provides
    @WeatherForecastScope
    fun provideLocationProvider(locationManager: LocationManager): LocationLocalDataSource =
        LocationLocalDataSource(locationManager)

    @Provides
    @WeatherForecastScope
    fun provideWeatherLruCache(): LruCache<String, Weather> = LruCache(
        MEMORY_CACHE_SIZE
    )
}