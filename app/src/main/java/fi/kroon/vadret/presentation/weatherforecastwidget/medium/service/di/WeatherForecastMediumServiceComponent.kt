package fi.kroon.vadret.presentation.weatherforecastwidget.medium.service.di

import dagger.Subcomponent
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.service.WeatherForecastMediumService

@Subcomponent(
    modules = [
        WeatherForecastMediumServiceModule::class
    ]
)
@WeatherForecastMediumServiceScope
interface WeatherForecastMediumServiceComponent {

    fun inject(weatherForecastMediumServiceFactory: WeatherForecastMediumService.WeatherForecastMediumServiceFactory)

    @Subcomponent.Builder
    interface Builder {
        fun weatherForecastMediumServiceModule(module: WeatherForecastMediumServiceModule): Builder
        fun build(): WeatherForecastMediumServiceComponent
    }
}