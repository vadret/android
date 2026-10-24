package fi.kroon.vadret.weatherforecast.di

import android.content.Context
import android.location.LocationManager
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.location.local.LocationLocalDataSource
import fi.kroon.vadret.weatherforecast.WeatherForecastView
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Module
@ExperimentalCoroutinesApi
object WeatherForecastModule {

    @Provides
    @WeatherForecastScope
    fun provideViewState(): WeatherForecastView.State = WeatherForecastView.State()

    @Provides
    @WeatherForecastScope
    fun provideLocationManager(context: Context): LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @Provides
    @WeatherForecastScope
    fun provideLocationProvider(locationManager: LocationManager): LocationLocalDataSource =
        LocationLocalDataSource(locationManager)
}