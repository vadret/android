package fi.kroon.vadret.presentation.weatherforecast.di

import dagger.Subcomponent
import fi.kroon.vadret.presentation.weatherforecast.WeatherForecastAdapter
import fi.kroon.vadret.presentation.weatherforecast.WeatherForecastView
import fi.kroon.vadret.presentation.weatherforecast.WeatherForecastViewModel
import fi.kroon.vadret.presentation.weatherforecast.autocomplete.AutoCompleteAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

@ExperimentalCoroutinesApi
@FlowPreview
@Subcomponent(
    modules = [
        WeatherForecastModule::class
    ]
)
@WeatherForecastScope
interface WeatherForecastComponent {

    /**
     *  ViewModel
     */
    fun provideWeatherForecastViewModel(): WeatherForecastViewModel
    fun provideEventChannel(): ConflatedBroadcastChannel<WeatherForecastView.Event>

    /**
     *  Adapter
     */
    fun provideWeatherForecastAdapter(): WeatherForecastAdapter
    fun provideAutoCompleteAdapter(): AutoCompleteAdapter

    @Subcomponent.Builder
    interface Builder {
        fun forecastModule(module: WeatherForecastModule): Builder
        fun build(): WeatherForecastComponent
    }
}