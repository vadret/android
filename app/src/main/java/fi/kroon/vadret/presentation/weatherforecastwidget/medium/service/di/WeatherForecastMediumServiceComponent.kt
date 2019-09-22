package fi.kroon.vadret.presentation.weatherforecastwidget.medium.service.di

import android.appwidget.AppWidgetManager
import dagger.Subcomponent
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.service.WeatherForecastMediumService
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.service.WeatherForecastMediumServiceView
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.service.WeatherForecastMediumServiceViewModel
import fi.kroon.vadret.util.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Subcomponent(
    modules = [
        WeatherForecastMediumServiceModule::class
    ]
)
@WeatherForecastMediumServiceScope
interface WeatherForecastMediumServiceComponent {

    fun inject(weatherForecastMediumServiceFactory: WeatherForecastMediumService.WeatherForecastMediumServiceFactory)

    /**
     *  AppWidgetManager
     */
    fun provideAppWidgetManager(): AppWidgetManager

    /**
     *  ViewModel
     */
    fun provideWeatherForecastMediumServiceViewModel(): WeatherForecastMediumServiceViewModel

    /**
     *  CompositeDisposable
     */
    fun provideCompositeDisposable(): CompositeDisposable

    /**
     *  PublishSubject
     */
    fun provideOnInitialised(): PublishSubject<WeatherForecastMediumServiceView.Event.OnInitialised>

    /**
     *  Scheduler
     */
    fun provideScheduler(): Scheduler

    @Subcomponent.Builder
    interface Builder {
        fun weatherForecastMediumServiceModule(module: WeatherForecastMediumServiceModule): Builder
        fun build(): WeatherForecastMediumServiceComponent
    }
}