package fi.kroon.vadret.di

import android.content.Context
import android.content.SharedPreferences
import android.location.LocationManager
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.aggregatedfeed.AggregatedFeedRepository
import fi.kroon.vadret.autocomplete.AutoCompleteRepository
import fi.kroon.vadret.common.DiskCache
import fi.kroon.vadret.library.LibraryRepository
import fi.kroon.vadret.location.LocationRepository
import fi.kroon.vadret.nominatim.NominatimRepository
import fi.kroon.vadret.radar.RadarRepository
import fi.kroon.vadret.theme.ThemeRepository
import fi.kroon.vadret.warningfilter.WarningFilterRepository
import fi.kroon.vadret.weatherforecast.WeatherForecastRepository
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.time.Clock
import dagger.Lazy as DaggerLazy

/**
 *  Repositories live as long as the process, so their caches and the
 *  database are shared by every screen. None of them builds the HTTP
 *  client before its first request.
 */
@Module
object RepositoryModule {

    @Provides
    @AppScope
    fun provideAggregatedFeedRepository(
        httpClient: DaggerLazy<HttpClient>,
        diskCache: DiskCache,
        clock: Clock,
        coroutineDispatcher: CoroutineDispatcher
    ): AggregatedFeedRepository =
        AggregatedFeedRepository(
            httpClient = httpClient.toKotlinLazy(),
            diskCache = diskCache,
            clock = clock,
            coroutineDispatcher = coroutineDispatcher
        )

    @Provides
    @AppScope
    fun provideAutoCompleteRepository(
        context: Context,
        coroutineDispatcher: CoroutineDispatcher
    ): AutoCompleteRepository =
        AutoCompleteRepository(
            context = context,
            coroutineDispatcher = coroutineDispatcher
        )

    @Provides
    @AppScope
    fun provideLibraryRepository(): LibraryRepository = LibraryRepository()

    @Provides
    @AppScope
    fun provideLocationRepository(
        context: Context,
        sharedPreferences: SharedPreferences,
        coroutineDispatcher: CoroutineDispatcher
    ): LocationRepository =
        LocationRepository(
            locationManager = context.getSystemService(LocationManager::class.java),
            sharedPreferences = sharedPreferences,
            coroutineDispatcher = coroutineDispatcher
        )

    @Provides
    @AppScope
    fun provideNominatimRepository(httpClient: DaggerLazy<HttpClient>): NominatimRepository =
        NominatimRepository(httpClient = httpClient.toKotlinLazy())

    @Provides
    @AppScope
    fun provideRadarRepository(
        httpClient: DaggerLazy<HttpClient>,
        diskCache: DiskCache,
        clock: Clock,
        coroutineDispatcher: CoroutineDispatcher
    ): RadarRepository =
        RadarRepository(
            httpClient = httpClient.toKotlinLazy(),
            diskCache = diskCache,
            clock = clock,
            coroutineDispatcher = coroutineDispatcher
        )

    @Provides
    @AppScope
    fun provideThemeRepository(
        sharedPreferences: SharedPreferences,
        coroutineDispatcher: CoroutineDispatcher
    ): ThemeRepository =
        ThemeRepository(
            sharedPreferences = sharedPreferences,
            coroutineDispatcher = coroutineDispatcher
        )

    @Provides
    @AppScope
    fun provideWarningFilterRepository(
        context: Context,
        httpClient: DaggerLazy<HttpClient>
    ): WarningFilterRepository =
        WarningFilterRepository(
            context = context,
            httpClient = httpClient.toKotlinLazy()
        )

    @Provides
    @AppScope
    fun provideWeatherForecastRepository(httpClient: DaggerLazy<HttpClient>): WeatherForecastRepository =
        WeatherForecastRepository(httpClient = httpClient.toKotlinLazy())

    private fun DaggerLazy<HttpClient>.toKotlinLazy(): Lazy<HttpClient> =
        lazy { get() }
}