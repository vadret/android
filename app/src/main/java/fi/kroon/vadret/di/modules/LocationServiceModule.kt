package fi.kroon.vadret.di.modules

import android.content.Context
import android.location.LocationManager
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.location.LocationProvider
import fi.kroon.vadret.data.location.LocationRepository
import fi.kroon.vadret.di.scope.VadretApplicationScope

@Module
class LocationServiceModule {

    @Module
    companion object {
        @Provides
        @JvmStatic
        @VadretApplicationScope
        fun provideLocationManager(context: Context) =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        @Provides
        @JvmStatic
        @VadretApplicationScope
        fun locationProvider(locationManager: LocationManager) = LocationProvider(locationManager)

        @Provides
        @JvmStatic
        @VadretApplicationScope
        fun locationRepository(locationProvider: LocationProvider) = LocationRepository(locationProvider)
    }
}