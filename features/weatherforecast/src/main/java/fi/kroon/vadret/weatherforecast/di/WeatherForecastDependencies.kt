package fi.kroon.vadret.weatherforecast.di

import fi.kroon.vadret.autocomplete.AutoCompleteRepository
import fi.kroon.vadret.location.LocationRepository
import fi.kroon.vadret.nominatim.NominatimRepository
import fi.kroon.vadret.weatherforecast.WeatherForecastRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.time.Clock

/**
 *  What the weather screen needs from the app.
 */
interface WeatherForecastDependencies {
    fun autoCompleteRepository(): AutoCompleteRepository
    fun locationRepository(): LocationRepository
    fun nominatimRepository(): NominatimRepository
    fun weatherForecastRepository(): WeatherForecastRepository
    fun clock(): Clock
    fun coroutineDispatcher(): CoroutineDispatcher
}