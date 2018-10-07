package fi.kroon.vadret.di.component

import android.app.Application
import dagger.Component
import fi.kroon.vadret.di.modules.NetworkModule
import fi.kroon.vadret.di.modules.ApiServiceModule
import fi.kroon.vadret.di.modules.SharedPreferencesModule
import fi.kroon.vadret.di.modules.SchedulerModule
import fi.kroon.vadret.di.modules.ViewModelModule
import fi.kroon.vadret.di.modules.LocationServiceModule
import fi.kroon.vadret.di.scope.VadretApplicationScope
import fi.kroon.vadret.presentation.AboutFragment
import fi.kroon.vadret.presentation.AlertFragment
import fi.kroon.vadret.presentation.MainActivity
import fi.kroon.vadret.presentation.RadarFragment
import fi.kroon.vadret.presentation.WeatherFragment

@VadretApplicationScope
@Component(
        modules = [
            NetworkModule::class,
            ApiServiceModule::class,
            SharedPreferencesModule::class,
            SchedulerModule::class,
            LocationServiceModule::class,
            ViewModelModule::class
        ]
)
interface VadretApplicationComponent {
    fun inject(application: Application)
    fun inject(mainActivity: MainActivity)
    fun inject(alertFragment: AlertFragment)
    fun inject(weatherFragment: WeatherFragment)
    fun inject(aboutFragment: AboutFragment)
    fun inject(radarFragment: RadarFragment)
}