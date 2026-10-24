package fi.kroon.vadret.weatherforecast.di

import androidx.compose.runtime.Composable
import dagger.Component
import fi.kroon.vadret.core.CoreComponent
import fi.kroon.vadret.ui.daggerViewModel
import fi.kroon.vadret.weatherforecast.WeatherForecastViewModel

@WeatherForecastScope
@Component(
    dependencies = [
        CoreComponent::class
    ],
    modules = [
        WeatherForecastModule::class
    ]
)
internal interface WeatherForecastComponent {

    fun weatherForecastViewModel(): WeatherForecastViewModel

    @Component.Factory
    interface Factory {
        fun create(coreComponent: CoreComponent): WeatherForecastComponent
    }
}

@Composable
internal fun weatherForecastViewModel(): WeatherForecastViewModel =
    daggerViewModel { _, coreComponent: CoreComponent ->
        DaggerWeatherForecastComponent
            .factory()
            .create(coreComponent = coreComponent)
            .weatherForecastViewModel()
    }