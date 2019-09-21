package fi.kroon.vadret.presentation.weatherforecastwidget.medium.service.di

import android.appwidget.AppWidgetManager
import android.content.Context
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.service.WeatherForecastMediumServiceView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Module
object WeatherForecastMediumServiceModule {

    @Provides
    @JvmStatic
    @WeatherForecastMediumServiceScope
    fun provideViewState(): WeatherForecastMediumServiceView.State = WeatherForecastMediumServiceView.State()

    @Provides
    @JvmStatic
    @WeatherForecastMediumServiceScope
    fun provideOnWidgetInitialisedSubject(): PublishSubject<WeatherForecastMediumServiceView.Event.OnInitialised> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastMediumServiceScope
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    @JvmStatic
    @WeatherForecastMediumServiceScope
    fun provideAppWidgetManager(context: Context): AppWidgetManager =
        AppWidgetManager
            .getInstance(context)
}