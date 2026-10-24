package fi.kroon.vadret.weatherforecast.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.Component
import fi.kroon.vadret.weatherforecast.WeatherForecastViewModel

@Component(
    dependencies = [
        WeatherForecastDependencies::class
    ],
    modules = [
        WeatherForecastModule::class
    ]
)
internal interface WeatherForecastComponent {

    fun weatherForecastViewModel(): WeatherForecastViewModel

    @Component.Factory
    interface Factory {
        fun create(weatherForecastDependencies: WeatherForecastDependencies): WeatherForecastComponent
    }
}

/**
 *  One view model per navigation entry.
 */
@Composable
internal fun weatherForecastViewModel(weatherForecastDependencies: WeatherForecastDependencies): WeatherForecastViewModel =
    viewModel {
        DaggerWeatherForecastComponent
            .factory()
            .create(weatherForecastDependencies = weatherForecastDependencies)
            .weatherForecastViewModel()
    }