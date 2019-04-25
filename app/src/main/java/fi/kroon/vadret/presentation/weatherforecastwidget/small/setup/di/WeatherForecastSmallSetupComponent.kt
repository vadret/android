package fi.kroon.vadret.presentation.weatherforecastwidget.small.setup.di

import dagger.Subcomponent
import fi.kroon.vadret.presentation.weatherforecastwidget.small.setup.WeatherForecastSmallSetup

@Subcomponent(
    modules = [
        WeatherForecastSmallSetupModule::class
    ]
)
@WeatherForecastSmallSetupScope
interface WeatherForecastSmallSetupComponent {

    fun inject(weatherForecastSmallSetup: WeatherForecastSmallSetup)

    @Subcomponent.Builder
    interface Builder {
        fun weatherForecastAppWidgetSmallSetupModule(module: WeatherForecastSmallSetupModule): Builder
        fun build(): WeatherForecastSmallSetupComponent
    }
}