package fi.kroon.vadret.di.component

import android.app.Application
import dagger.Component
import fi.kroon.vadret.di.modules.*
import fi.kroon.vadret.di.scope.VadretApplicationScope
import fi.kroon.vadret.presentation.AboutFragment
import fi.kroon.vadret.presentation.MainActivity
import fi.kroon.vadret.presentation.WeatherFragment

@VadretApplicationScope
@Component(
        modules = [
            NetworkModule::class,
            ApiServiceModule::class,
            SharedPreferencesModule::class,
            SchedulerModule::class,
            ViewModelModule::class
        ]
)
interface VadretApplicationComponent {

    fun inject(application: Application)
    fun inject(mainActivity: MainActivity)
    fun inject(weatherFragment: WeatherFragment)
    fun inject(aboutFragment: AboutFragment)

}