package fi.kroon.vadret.presentation.weatherforecastwidget.tiny.setup.di

import dagger.Subcomponent
import fi.kroon.vadret.presentation.weatherforecastwidget.tiny.setup.WeatherForecastTinySetup

@Subcomponent(
    modules = [
        WeatherForecastTinySetupModule::class
    ]
)
@WeatherForecastTinySetupScope
interface WeatherForecastTinySetupComponent {

    fun inject(weatherForecastTinySetup: WeatherForecastTinySetup)

    @Subcomponent.Builder
    interface Builder {
        fun weatherForecastAppWidgetTinySetupModule(module: WeatherForecastTinySetupModule): Builder
        fun build(): WeatherForecastTinySetupComponent
    }
}