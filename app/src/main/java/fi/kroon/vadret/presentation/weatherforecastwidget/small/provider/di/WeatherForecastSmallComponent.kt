package fi.kroon.vadret.presentation.weatherforecastwidget.small.provider.di

import android.content.Context
import dagger.Subcomponent
import fi.kroon.vadret.presentation.weatherforecastwidget.small.provider.WeatherForecastSmallAppWidgetProvider
import fi.kroon.vadret.presentation.weatherforecastwidget.small.provider.WeatherForecastSmallView
import fi.kroon.vadret.presentation.weatherforecastwidget.small.provider.WeatherForecastSmallViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Subcomponent(
    modules = [
        WeatherForecastSmallModule::class
    ]
)
@WeatherForecastSmallFeatureScope
interface WeatherForecastSmallComponent {

    fun inject(weatherForecastSmallAppWidgetProvider: WeatherForecastSmallAppWidgetProvider)

    /**
     *  PublishSubject
     */
    fun provideOnWidgetInitialised(): PublishSubject<WeatherForecastSmallView.Event.OnWidgetInitialised>
    fun provideOnWidgetUpdated(): PublishSubject<WeatherForecastSmallView.Event.OnWidgetUpdated>
    fun provideOnBootCompleted(): PublishSubject<WeatherForecastSmallView.Event.OnBootCompleted>

    /**
     *  CompositeDisposable
     */
    fun provideCompositeDisposable(): CompositeDisposable

    /**
     *  ViewModel
     */
    fun provideWeatherForecastSmallViewModel(): WeatherForecastSmallViewModel

    /**
     *  Context
     */
    fun provideContext(): Context

    @Subcomponent.Builder
    interface Builder {
        fun weatherForecastSmallModule(module: WeatherForecastSmallModule): Builder
        fun build(): WeatherForecastSmallComponent
    }
}