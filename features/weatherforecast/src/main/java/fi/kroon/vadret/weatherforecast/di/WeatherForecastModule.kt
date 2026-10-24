package fi.kroon.vadret.weatherforecast.di

import android.content.Context
import android.location.LocationManager
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.location.local.LocationLocalDataSource

@Module
internal object WeatherForecastModule {

    @Provides
    @WeatherForecastScope
    fun provideLocationManager(context: Context): LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @Provides
    @WeatherForecastScope
    fun provideLocationProvider(locationManager: LocationManager): LocationLocalDataSource =
        LocationLocalDataSource(locationManager)
}