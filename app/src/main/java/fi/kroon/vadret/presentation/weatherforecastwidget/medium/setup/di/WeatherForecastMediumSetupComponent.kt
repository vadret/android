package fi.kroon.vadret.presentation.weatherforecastwidget.medium.setup.di

import dagger.Subcomponent
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.setup.WeatherForecastMediumSetup

@Subcomponent(
    modules = [
        WeatherForecastMediumSetupModule::class
    ]
)
@WeatherForecastMediumSetupScope
interface WeatherForecastMediumSetupComponent {

    fun inject(weatherForecastMediumSetup: WeatherForecastMediumSetup)

    @Subcomponent.Builder
    interface Builder {
        fun weatherForecastAppWidgetMediumSetupModule(module: WeatherForecastMediumSetupModule): Builder
        fun build(): WeatherForecastMediumSetupComponent
    }
}