package fi.kroon.vadret.presentation.weatherforecast.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import fi.kroon.vadret.core.CoreComponent
import fi.kroon.vadret.presentation.weatherforecast.WeatherForecastAdapter
import fi.kroon.vadret.presentation.weatherforecast.WeatherForecastView
import fi.kroon.vadret.presentation.weatherforecast.WeatherForecastViewModel
import fi.kroon.vadret.presentation.weatherforecast.autocomplete.AutoCompleteAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

@FlowPreview
@ExperimentalCoroutinesApi
@WeatherForecastScope
@Component(
    modules = [
        WeatherForecastModule::class
    ],
    dependencies = [
        CoreComponent::class
    ]
)
interface WeatherForecastComponent {

    fun provideWeatherForecastViewModel(): WeatherForecastViewModel
    fun provideEventChannel(): ConflatedBroadcastChannel<WeatherForecastView.Event>

    fun provideWeatherForecastAdapter(): WeatherForecastAdapter
    fun provideAutoCompleteAdapter(): AutoCompleteAdapter

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance
            context: Context,
            coreComponent: CoreComponent
        ): WeatherForecastComponent
    }
}