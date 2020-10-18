package fi.kroon.vadret.di.component

import android.app.Application
import com.afollestad.rxkprefs.RxkPrefs
import dagger.Component
import fi.kroon.vadret.di.modules.ApiServiceModule
import fi.kroon.vadret.di.modules.CacheModule
import fi.kroon.vadret.di.modules.DatabaseModule
import fi.kroon.vadret.di.modules.LocationServiceModule
import fi.kroon.vadret.di.modules.NetworkModule
import fi.kroon.vadret.di.modules.RxkPrefsModule
import fi.kroon.vadret.di.modules.SchedulerModule
import fi.kroon.vadret.di.modules.ServiceModule
import fi.kroon.vadret.di.scope.CoreApplicationScope
import fi.kroon.vadret.presentation.main.di.MainActivityComponent
import fi.kroon.vadret.presentation.radar.di.RadarComponent
import fi.kroon.vadret.presentation.warning.display.di.WarningComponent
import fi.kroon.vadret.presentation.warning.filter.di.WarningFilterComponent
import fi.kroon.vadret.presentation.weatherforecast.di.WeatherForecastComponent

@CoreApplicationScope
@Component(
    modules = [
        ApiServiceModule::class,
        CacheModule::class,
        DatabaseModule::class,
        NetworkModule::class,
        ServiceModule::class,
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

    fun warningComponentBuilder(): WarningComponent.Builder
    fun warningFilterComponentBuilder(): WarningFilterComponent.Builder

    fun weatherForecastComponentBuilder(): WeatherForecastComponent.Builder
}