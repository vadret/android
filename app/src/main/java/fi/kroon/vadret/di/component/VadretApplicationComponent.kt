package fi.kroon.vadret.di.component

import android.app.Application
import com.afollestad.rxkprefs.RxkPrefs
import dagger.Component
import fi.kroon.vadret.di.modules.ApiServiceModule
import fi.kroon.vadret.di.modules.BindingModule
import fi.kroon.vadret.di.modules.CacheModule
import fi.kroon.vadret.di.modules.LocationServiceModule
import fi.kroon.vadret.di.modules.NetworkModule
import fi.kroon.vadret.di.modules.RxkPrefsModule
import fi.kroon.vadret.di.modules.SchedulerModule
import fi.kroon.vadret.di.modules.ViewModelModule
import fi.kroon.vadret.di.scope.VadretApplicationScope
import fi.kroon.vadret.presentation.AlertFragment
import fi.kroon.vadret.presentation.MainActivity
import fi.kroon.vadret.presentation.RadarFragment
import fi.kroon.vadret.presentation.aboutapp.di.AboutAppComponent
import fi.kroon.vadret.presentation.weatherforecast.di.WeatherForecastComponent

@VadretApplicationScope
@Component(
    modules = [
        NetworkModule::class,
        ApiServiceModule::class,
        CacheModule::class,
        RxkPrefsModule::class,
        SchedulerModule::class,
        LocationServiceModule::class,
        ViewModelModule::class,
        BindingModule::class
    ]
)
interface VadretApplicationComponent {

    fun inject(application: Application)
    fun inject(mainActivity: MainActivity)
    fun inject(alertFragment: AlertFragment)
    fun inject(radarFragment: RadarFragment)

    fun rxkPrefs(rxkPrefs: RxkPrefs)

    fun appAboutComponentBuilder(): AboutAppComponent.Builder
    fun forecastComponentBuilder(): WeatherForecastComponent.Builder
}