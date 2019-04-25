package fi.kroon.vadret.di.component

import android.app.Application
import com.afollestad.rxkprefs.RxkPrefs
import dagger.Component
import fi.kroon.vadret.di.modules.ApiServiceModule
import fi.kroon.vadret.di.modules.CacheModule
import fi.kroon.vadret.di.modules.LocationServiceModule
import fi.kroon.vadret.di.modules.NetworkModule
import fi.kroon.vadret.di.modules.RxkPrefsModule
import fi.kroon.vadret.di.modules.SchedulerModule
import fi.kroon.vadret.di.scope.CoreApplicationScope
import fi.kroon.vadret.presentation.aboutapp.di.AboutAppComponent
import fi.kroon.vadret.presentation.alert.di.AlertComponent
import fi.kroon.vadret.presentation.main.di.MainActivityComponent
import fi.kroon.vadret.presentation.radar.di.RadarComponent
import fi.kroon.vadret.presentation.weatherforecast.di.WeatherForecastComponent
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.provider.di.WeatherForecastMediumComponent
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.service.di.WeatherForecastMediumServiceComponent
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.setup.di.WeatherForecastMediumSetupComponent
import fi.kroon.vadret.presentation.weatherforecastwidget.small.provider.di.WeatherForecastSmallComponent
import fi.kroon.vadret.presentation.weatherforecastwidget.small.setup.di.WeatherForecastSmallSetupComponent
import fi.kroon.vadret.presentation.weatherforecastwidget.tiny.provider.di.WeatherForecastTinyComponent
import fi.kroon.vadret.presentation.weatherforecastwidget.tiny.setup.di.WeatherForecastTinySetupComponent

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
    fun weatherForecastComponentBuilder(): WeatherForecastComponent.Builder

    fun weatherForecastTinyWidgetComponentBuilder(): WeatherForecastTinyComponent.Builder
    fun weatherForecastTinyAppWidgetSetupComponentBuilder(): WeatherForecastTinySetupComponent.Builder

    fun weatherForecastSmallWidgetComponentBuilder(): WeatherForecastSmallComponent.Builder
    fun weatherForecastSmallAppWidgetSetupComponentBuilder(): WeatherForecastSmallSetupComponent.Builder

    fun weatherForecastMediumWidgetComponentBuilder(): WeatherForecastMediumComponent.Builder
    fun weatherForecastMediumAppWidgetSetupComponentBuilder(): WeatherForecastMediumSetupComponent.Builder
    fun weatherForecastMediumServiceComponentBuilder(): WeatherForecastMediumServiceComponent.Builder
}