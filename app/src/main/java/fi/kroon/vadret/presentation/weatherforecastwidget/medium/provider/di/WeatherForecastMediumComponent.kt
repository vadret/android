package fi.kroon.vadret.presentation.weatherforecastwidget.medium.provider.di

import android.content.Context
import dagger.Subcomponent
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.provider.WeatherForecastMediumAppWidgetProvider
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.provider.WeatherForecastMediumView
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.provider.WeatherForecastMediumViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Subcomponent(
    modules = [
        WeatherForecastMediumModule::class
    ]
)
@WeatherForecastMediumFeatureScope
interface WeatherForecastMediumComponent {

    fun inject(weatherForecastMediumAppWidgetProvider: WeatherForecastMediumAppWidgetProvider)

    fun provideWeatherForecastMediumViewModel(): WeatherForecastMediumViewModel
    fun provideCompositeDisposable(): CompositeDisposable
    fun provideOnWidgetInitialised(): PublishSubject<WeatherForecastMediumView.Event.OnWidgetInitialised>
    fun provideOnWidgetUpdated(): PublishSubject<WeatherForecastMediumView.Event.OnWidgetUpdated>
    fun provideOnBootCompleted(): PublishSubject<WeatherForecastMediumView.Event.OnBootCompleted>
    fun provideContext(): Context

    @Subcomponent.Builder
    interface Builder {
        fun weatherForecastMediumModule(module: WeatherForecastMediumModule): Builder
        fun build(): WeatherForecastMediumComponent
    }
}