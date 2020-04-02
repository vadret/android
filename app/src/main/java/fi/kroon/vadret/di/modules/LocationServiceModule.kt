package fi.kroon.vadret.di.modules

import android.content.Context
import android.location.LocationManager
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.location.local.LocationLocalDataSource
import fi.kroon.vadret.di.scope.CoreApplicationScope

@Module
object LocationServiceModule {

    @Provides
    @CoreApplicationScope
    fun provideLocationManager(context: Context): LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @Provides
    @CoreApplicationScope
    fun provideLocationProvider(locationManager: LocationManager): LocationLocalDataSource =
        LocationLocalDataSource(locationManager)
}