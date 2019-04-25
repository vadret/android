package fi.kroon.vadret.presentation.weatherforecastwidget.medium.setup.di

import android.appwidget.AppWidgetManager
import android.content.Context
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.presentation.weatherforecast.autocomplete.AutoCompleteAdapter
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.setup.WeatherForecastMediumSetupView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Module
object WeatherForecastMediumSetupModule {

    @Provides
    @JvmStatic
    @WeatherForecastMediumSetupScope
    fun provideOnWidgetSetupInitialisedSubject(): PublishSubject<WeatherForecastMediumSetupView.Event.OnSetupInitialised> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastMediumSetupScope
    fun provideOnLocationPermissionDeniedSubject(): PublishSubject<WeatherForecastMediumSetupView.Event.OnLocationPermissionDenied> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastMediumSetupScope
    fun provideOnLocalitySearchEnabledSubject(): PublishSubject<WeatherForecastMediumSetupView.Event.OnLocalitySearchEnabled> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastMediumSetupScope
    fun provideOnLocalitySearchDisabledSubject(): PublishSubject<WeatherForecastMediumSetupView.Event.OnLocalitySearchDisabled> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastMediumSetupScope
    fun provideOnConfigurationConfirmedSubject(): PublishSubject<WeatherForecastMediumSetupView.Event.OnConfigurationConfirmed> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastMediumSetupScope
    fun provideOnCancelledClickedSubject(): PublishSubject<WeatherForecastMediumSetupView.Event.OnCanceledClicked> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastMediumSetupScope
    fun provideOnSearchViewDismissedSubject(): PublishSubject<WeatherForecastMediumSetupView.Event.OnSearchViewDismissed> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastMediumSetupScope
    fun provideOnLocalityTextUpdatedSubject(): PublishSubject<WeatherForecastMediumSetupView.Event.OnLocalityTextUpdated> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastMediumSetupScope
    fun provideAppWidgetManager(context: Context): AppWidgetManager =
        AppWidgetManager
            .getInstance(context)

    @Provides
    @JvmStatic
    @WeatherForecastMediumSetupScope
    fun provideOnAutoCompleteItemItemClickedSubject(): PublishSubject<WeatherForecastMediumSetupView.Event.OnAutoCompleteItemClicked> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastMediumSetupScope
    fun provideAutoCompleteClickSubject(): PublishSubject<AutoCompleteItem> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastMediumSetupScope
    fun provideAutoCompleteAdapter(
        clickSubject: PublishSubject<AutoCompleteItem>
    ): AutoCompleteAdapter =
        AutoCompleteAdapter(clickSubject)

    @Provides
    @JvmStatic
    @WeatherForecastMediumSetupScope
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    @JvmStatic
    @WeatherForecastMediumSetupScope
    fun provideState(): WeatherForecastMediumSetupView.State = WeatherForecastMediumSetupView.State()
}