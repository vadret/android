package fi.kroon.vadret.presentation.weatherforecastwidget.small.provider.di

import android.appwidget.AppWidgetManager
import android.content.Context
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.presentation.weatherforecastwidget.small.provider.WeatherForecastSmallView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Module
object WeatherForecastSmallModule {

    @Provides
    @JvmStatic
    @WeatherForecastSmallFeatureScope
    fun provideViewState(): WeatherForecastSmallView.State = WeatherForecastSmallView.State()

    @Provides
    @JvmStatic
    @WeatherForecastSmallFeatureScope
    fun provideOnWidgetInitialisedSubject(): PublishSubject<WeatherForecastSmallView.Event.OnWidgetInitialised> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastSmallFeatureScope
    fun provideOnWidgetUpdatedSubject(): PublishSubject<WeatherForecastSmallView.Event.OnWidgetUpdated> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastSmallFeatureScope
    fun provideOnBootCompletedSubject(): PublishSubject<WeatherForecastSmallView.Event.OnBootCompleted> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastSmallFeatureScope
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    @JvmStatic
    @WeatherForecastSmallFeatureScope
    fun provideAppWidgetManager(context: Context): AppWidgetManager =
        AppWidgetManager
            .getInstance(context)
}