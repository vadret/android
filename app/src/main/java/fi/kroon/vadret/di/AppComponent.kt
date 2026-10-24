package fi.kroon.vadret.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import fi.kroon.vadret.forecastwarning.di.WarningDependencies
import fi.kroon.vadret.main.MainViewModel
import fi.kroon.vadret.radar.di.RadarDependencies
import fi.kroon.vadret.settings.di.SettingsDependencies
import fi.kroon.vadret.weatherforecast.di.WeatherForecastDependencies

/**
 *  Each feature declares what it needs as an interface, so a
 *  feature only ever sees the repositories it uses.
 */
@AppScope
@Component(
    modules = [
        ClockModule::class,
        DispatcherModule::class,
        MainModule::class,
        NetworkModule::class,
        RepositoryModule::class,
        StorageModule::class
    ]
)
internal interface AppComponent :
    RadarDependencies,
    SettingsDependencies,
    WarningDependencies,
    WeatherForecastDependencies {

    fun mainViewModel(): MainViewModel

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            context: Context
        ): AppComponent
    }
}