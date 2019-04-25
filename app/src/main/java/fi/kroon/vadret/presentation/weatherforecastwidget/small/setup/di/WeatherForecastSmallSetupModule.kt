package fi.kroon.vadret.presentation.weatherforecastwidget.small.setup.di

import android.appwidget.AppWidgetManager
import android.content.Context
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.presentation.weatherforecast.autocomplete.AutoCompleteAdapter
import fi.kroon.vadret.presentation.weatherforecastwidget.small.setup.WeatherForecastSmallSetupView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Module
object WeatherForecastSmallSetupModule {

    @Provides
    @JvmStatic
    @WeatherForecastSmallSetupScope
    fun provideOnWidgetSetupInitialisedSubject(): PublishSubject<WeatherForecastSmallSetupView.Event.OnSetupInitialised> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastSmallSetupScope
    fun provideOnLocationPermissionDeniedSubject(): PublishSubject<WeatherForecastSmallSetupView.Event.OnLocationPermissionDenied> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastSmallSetupScope
    fun provideOnLocalitySearchEnabledSubject(): PublishSubject<WeatherForecastSmallSetupView.Event.OnLocalitySearchEnabled> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastSmallSetupScope
    fun provideOnLocalitySearchDisabledSubject(): PublishSubject<WeatherForecastSmallSetupView.Event.OnLocalitySearchDisabled> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastSmallSetupScope
    fun provideOnConfigurationConfirmedSubject(): PublishSubject<WeatherForecastSmallSetupView.Event.OnConfigurationConfirmed> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastSmallSetupScope
    fun provideOnCancelledClickedSubject(): PublishSubject<WeatherForecastSmallSetupView.Event.OnCanceledClicked> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastSmallSetupScope
    fun provideOnSearchViewDismissedSubject(): PublishSubject<WeatherForecastSmallSetupView.Event.OnSearchViewDismissed> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastSmallSetupScope
    fun provideOnLocalityTextUpdatedSubject(): PublishSubject<WeatherForecastSmallSetupView.Event.OnLocalityTextUpdated> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastSmallSetupScope
    fun provideAppWidgetManager(context: Context): AppWidgetManager =
        AppWidgetManager
            .getInstance(context)

    @Provides
    @JvmStatic
    @WeatherForecastSmallSetupScope
    fun provideOnAutoCompleteItemItemClickedSubject(): PublishSubject<WeatherForecastSmallSetupView.Event.OnAutoCompleteItemClicked> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastSmallSetupScope
    fun provideAutoCompleteClickSubject(): PublishSubject<AutoCompleteItem> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastSmallSetupScope
    fun provideAutoCompleteAdapter(
        clickSubject: PublishSubject<AutoCompleteItem>
    ): AutoCompleteAdapter =
        AutoCompleteAdapter(clickSubject)

    @Provides
    @JvmStatic
    @WeatherForecastSmallSetupScope
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    @JvmStatic
    @WeatherForecastSmallSetupScope
    fun provideState(): WeatherForecastSmallSetupView.State = WeatherForecastSmallSetupView.State()
}