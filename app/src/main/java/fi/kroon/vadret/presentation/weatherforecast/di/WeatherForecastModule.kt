package fi.kroon.vadret.presentation.weatherforecast.di

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.presentation.weatherforecast.WeatherForecastView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

@Module
@ExperimentalCoroutinesApi
object WeatherForecastModule {

    @Provides
    @WeatherForecastScope
    fun provideEventChannel(): ConflatedBroadcastChannel<WeatherForecastView.Event> =
        ConflatedBroadcastChannel()

    @Provides
    @WeatherForecastScope
    fun provideViewState(): WeatherForecastView.State = WeatherForecastView.State()
}