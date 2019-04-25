package fi.kroon.vadret.presentation.weatherforecastwidget.tiny.provider.di

import dagger.Subcomponent
import fi.kroon.vadret.presentation.weatherforecastwidget.tiny.provider.WeatherForecastTinyAppWidgetProvider

@Subcomponent(
    modules = [
        WeatherForecastTinyModule::class
    ]
)
@WeatherForecastTinyFeatureScope
interface WeatherForecastTinyComponent {

    fun inject(weatherForecastTinyAppWidgetProvider: WeatherForecastTinyAppWidgetProvider)

    @Subcomponent.Builder
    interface Builder {
        fun weatherForecastTinyModule(module: WeatherForecastTinyModule): Builder
        fun build(): WeatherForecastTinyComponent
    }
}