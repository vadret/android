package fi.kroon.vadret.di.modules

import android.content.Context
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
        fun locationProvider(context: Context) = LocationProvider(context)

        @Provides
        @JvmStatic
        @VadretApplicationScope
        fun locationRepository(locationProvider: LocationProvider) = LocationRepository(locationProvider)
    }
}