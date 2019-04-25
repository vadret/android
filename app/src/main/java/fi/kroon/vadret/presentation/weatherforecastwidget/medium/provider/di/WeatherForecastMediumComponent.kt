package fi.kroon.vadret.presentation.weatherforecastwidget.medium.provider.di

import dagger.Subcomponent
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.provider.WeatherForecastMediumAppWidgetProvider

@Subcomponent(
    modules = [
        WeatherForecastMediumModule::class
    ]
)
@WeatherForecastMediumFeatureScope
interface WeatherForecastMediumComponent {

    fun inject(weatherForecastMediumAppWidgetProvider: WeatherForecastMediumAppWidgetProvider)

    @Subcomponent.Builder
    interface Builder {
        fun weatherForecastMediumModule(module: WeatherForecastMediumModule): Builder
        fun build(): WeatherForecastMediumComponent
    }
}