package fi.kroon.vadret.presentation.weatherforecastwidget.small.provider.di

import dagger.Subcomponent
import fi.kroon.vadret.presentation.weatherforecastwidget.small.provider.WeatherForecastSmallAppWidgetProvider

@Subcomponent(
    modules = [
        WeatherForecastSmallModule::class
    ]
)
@WeatherForecastSmallFeatureScope
interface WeatherForecastSmallComponent {

    fun inject(weatherForecastSmallAppWidgetProvider: WeatherForecastSmallAppWidgetProvider)

    @Subcomponent.Builder
    interface Builder {
        fun weatherForecastSmallModule(module: WeatherForecastSmallModule): Builder
        fun build(): WeatherForecastSmallComponent
    }
}