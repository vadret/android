package fi.kroon.vadret.weatherforecast.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import fi.kroon.vadret.core.CoreComponent
import fi.kroon.vadret.weatherforecast.WeatherForecastAdapter
import fi.kroon.vadret.weatherforecast.WeatherForecastViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@WeatherForecastScope
@Component(
    modules = [
        WeatherForecastModule::class
    ],
    dependencies = [
        CoreComponent::class
    ]
)
interface WeatherForecastComponent {

    fun provideWeatherForecastViewModel(): WeatherForecastViewModel
    fun provideWeatherForecastAdapter(): WeatherForecastAdapter

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            context: Context,
            coreComponent: CoreComponent
        ): WeatherForecastComponent
    }
}