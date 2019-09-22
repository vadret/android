package fi.kroon.vadret.presentation.weatherforecastwidget.tiny.provider.di

import android.content.Context
import dagger.Subcomponent
import fi.kroon.vadret.presentation.weatherforecastwidget.tiny.provider.WeatherForecastTinyAppWidgetProvider
import fi.kroon.vadret.presentation.weatherforecastwidget.tiny.provider.WeatherForecastTinyView
import fi.kroon.vadret.presentation.weatherforecastwidget.tiny.provider.WeatherForecastTinyViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Subcomponent(
    modules = [
        WeatherForecastTinyModule::class
    ]
)
@WeatherForecastTinyFeatureScope
interface WeatherForecastTinyComponent {

    fun inject(weatherForecastTinyAppWidgetProvider: WeatherForecastTinyAppWidgetProvider)

    fun provideWeatherForecastTinyViewModel(): WeatherForecastTinyViewModel
    fun provideCompositeDisposable(): CompositeDisposable

    fun provideOnWidgetInitialised(): PublishSubject<WeatherForecastTinyView.Event.OnWidgetInitialised>
    fun provideOnWidgetUpdated(): PublishSubject<WeatherForecastTinyView.Event.OnWidgetUpdated>
    fun provideOnBootCompleted(): PublishSubject<WeatherForecastTinyView.Event.OnBootCompleted>
    fun provideContext(): Context

    @Subcomponent.Builder
    interface Builder {
        fun weatherForecastTinyModule(module: WeatherForecastTinyModule): Builder
        fun build(): WeatherForecastTinyComponent
    }
}