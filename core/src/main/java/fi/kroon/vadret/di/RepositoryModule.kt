package fi.kroon.vadret.di

import android.content.Context
import android.location.LocationManager
import fi.kroon.vadret.aggregatedfeed.AggregatedFeedRepository
import fi.kroon.vadret.autocomplete.AutoCompleteRepository
import fi.kroon.vadret.library.LibraryRepository
import fi.kroon.vadret.location.LocationRepository
import fi.kroon.vadret.nominatim.NominatimRepository
import fi.kroon.vadret.radar.RadarRepository
import fi.kroon.vadret.theme.ThemeRepository
import fi.kroon.vadret.warningfilter.WarningFilterRepository
import fi.kroon.vadret.weatherforecast.WeatherForecastRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val repositoryModule: Module = module {
    single<AggregatedFeedRepository> {
        AggregatedFeedRepository(
            httpClient = inject(),
            diskCache = get(),
            clock = get(),
            coroutineDispatcher = get()
        )
    }
    single<AutoCompleteRepository> {
        AutoCompleteRepository(
            context = get(),
            coroutineDispatcher = get()
        )
    }
    single<LibraryRepository> { LibraryRepository() }
    single<LocationRepository> {
        LocationRepository(
            locationManager = get<Context>().getSystemService(LocationManager::class.java),
            dataStore = get(),
            coroutineDispatcher = get()
        )
    }
    single<NominatimRepository> { NominatimRepository(httpClient = inject()) }
    single<RadarRepository> {
        RadarRepository(
            httpClient = inject(),
            diskCache = get(),
            clock = get(),
            coroutineDispatcher = get()
        )
    }
    single<ThemeRepository> { ThemeRepository(dataStore = get()) }
    single<WarningFilterRepository> {
        WarningFilterRepository(
            context = get(),
            httpClient = inject()
        )
    }
    single<WeatherForecastRepository> { WeatherForecastRepository(httpClient = inject()) }
}