package fi.kroon.vadret.presentation.weatherforecast.di

import dagger.Subcomponent
import fi.kroon.vadret.presentation.weatherforecast.WeatherForecastFragment

@Subcomponent(
    modules = [
        WeatherForecastModule::class
    ]
)
@WeatherForecastScope
interface WeatherForecastComponent {

    fun inject(weatherForecastFragment: WeatherForecastFragment)

    @Subcomponent.Builder
    interface Builder {
        fun forecastModule(module: WeatherForecastModule): Builder
        fun build(): WeatherForecastComponent
    }
}