package fi.kroon.vadret.core.di.component

import android.app.Application
import com.afollestad.rxkprefs.RxkPrefs
import dagger.Component
import fi.kroon.vadret.core.di.modules.ApiServiceModule
import fi.kroon.vadret.core.di.modules.CacheModule
import fi.kroon.vadret.core.di.modules.LocationServiceModule
import fi.kroon.vadret.core.di.modules.NetworkModule
import fi.kroon.vadret.core.di.modules.RxkPrefsModule
import fi.kroon.vadret.core.di.modules.SchedulerModule
import fi.kroon.vadret.core.di.scope.CoreApplicationScope
import fi.kroon.vadret.presentation.aboutapp.di.AboutAppComponent
import fi.kroon.vadret.presentation.alert.di.AlertComponent
import fi.kroon.vadret.presentation.main.di.MainActivityComponent
import fi.kroon.vadret.presentation.radar.di.RadarComponent
import fi.kroon.vadret.presentation.weatherforecast.di.WeatherForecastComponent

@CoreApplicationScope
@Component(
    modules = [
        NetworkModule::class,
        ApiServiceModule::class,
        CacheModule::class,
        RxkPrefsModule::class,
        SchedulerModule::class,
        LocationServiceModule::class
    ]
)
interface CoreApplicationComponent {

    fun inject(application: Application)

    fun rxkPrefs(rxkPrefs: RxkPrefs)

    fun mainActivityComponentBuilder(): MainActivityComponent.Builder
    fun radarComponentBuilder(): RadarComponent.Builder
    fun appAboutComponentBuilder(): AboutAppComponent.Builder
    fun alertComponentBuilder(): AlertComponent.Builder
    fun forecastComponentBuilder(): WeatherForecastComponent.Builder
}