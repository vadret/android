package fi.kroon.vadret.presentation.weatherforecastwidget.medium.provider.di

import android.appwidget.AppWidgetManager
import android.content.Context
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.provider.WeatherForecastMediumView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Module
object WeatherForecastMediumModule {

    @Provides
    @JvmStatic
    @WeatherForecastMediumFeatureScope
    fun provideViewState(): WeatherForecastMediumView.State = WeatherForecastMediumView.State()

    @Provides
    @JvmStatic
    @WeatherForecastMediumFeatureScope
    fun provideOnWidgetInitialisedSubject(): PublishSubject<WeatherForecastMediumView.Event.OnWidgetInitialised> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastMediumFeatureScope
    fun provideOnWidgetUpdatedSubject(): PublishSubject<WeatherForecastMediumView.Event.OnWidgetUpdated> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastMediumFeatureScope
    fun provideOnBootCompletedSubject(): PublishSubject<WeatherForecastMediumView.Event.OnBootCompleted> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastMediumFeatureScope
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    @JvmStatic
    @WeatherForecastMediumFeatureScope
    fun provideAppWidgetManager(context: Context): AppWidgetManager =
        AppWidgetManager
            .getInstance(context)
}