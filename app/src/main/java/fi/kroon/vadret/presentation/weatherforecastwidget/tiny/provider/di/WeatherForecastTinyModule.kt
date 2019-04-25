package fi.kroon.vadret.presentation.weatherforecastwidget.tiny.provider.di

import android.appwidget.AppWidgetManager
import android.content.Context
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.presentation.weatherforecastwidget.tiny.provider.WeatherForecastTinyView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Module
object WeatherForecastTinyModule {

    @Provides
    @JvmStatic
    @WeatherForecastTinyFeatureScope
    fun provideViewState(): WeatherForecastTinyView.State = WeatherForecastTinyView.State()

    @Provides
    @JvmStatic
    @WeatherForecastTinyFeatureScope
    fun provideOnWidgetInitialisedSubject(): PublishSubject<WeatherForecastTinyView.Event.OnWidgetInitialised> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastTinyFeatureScope
    fun provideOnWidgetUpdatedSubject(): PublishSubject<WeatherForecastTinyView.Event.OnWidgetUpdated> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastTinyFeatureScope
    fun provideOnBootCompletedSubject(): PublishSubject<WeatherForecastTinyView.Event.OnBootCompleted> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastTinyFeatureScope
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    @JvmStatic
    @WeatherForecastTinyFeatureScope
    fun provideAppWidgetManager(context: Context): AppWidgetManager =
        AppWidgetManager
            .getInstance(context)
}