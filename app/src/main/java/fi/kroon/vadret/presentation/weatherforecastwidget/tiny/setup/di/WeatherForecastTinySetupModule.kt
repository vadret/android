package fi.kroon.vadret.presentation.weatherforecastwidget.tiny.setup.di

import android.appwidget.AppWidgetManager
import android.content.Context
import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.presentation.weatherforecast.autocomplete.AutoCompleteAdapter
import fi.kroon.vadret.presentation.weatherforecastwidget.tiny.setup.WeatherForecastTinySetupView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Module
object WeatherForecastTinySetupModule {

    @Provides
    @JvmStatic
    @WeatherForecastTinySetupScope
    fun provideOnWidgetSetupInitialisedSubject(): PublishSubject<WeatherForecastTinySetupView.Event.OnSetupInitialised> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastTinySetupScope
    fun provideOnLocationPermissionDeniedSubject(): PublishSubject<WeatherForecastTinySetupView.Event.OnLocationPermissionDenied> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastTinySetupScope
    fun provideOnLocalitySearchEnabledSubject(): PublishSubject<WeatherForecastTinySetupView.Event.OnLocalitySearchEnabled> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastTinySetupScope
    fun provideOnLocalitySearchDisabledSubject(): PublishSubject<WeatherForecastTinySetupView.Event.OnLocalitySearchDisabled> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastTinySetupScope
    fun provideOnConfigurationConfirmedSubject(): PublishSubject<WeatherForecastTinySetupView.Event.OnConfigurationConfirmed> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastTinySetupScope
    fun provideOnCancelledClickedSubject(): PublishSubject<WeatherForecastTinySetupView.Event.OnCanceledClicked> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastTinySetupScope
    fun provideOnSearchViewDismissedSubject(): PublishSubject<WeatherForecastTinySetupView.Event.OnSearchViewDismissed> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastTinySetupScope
    fun provideOnLocalityTextUpdatedSubject(): PublishSubject<WeatherForecastTinySetupView.Event.OnLocalityTextUpdated> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastTinySetupScope
    fun provideAppWidgetManager(context: Context): AppWidgetManager =
        AppWidgetManager
            .getInstance(context)

    @Provides
    @JvmStatic
    @WeatherForecastTinySetupScope
    fun provideOnAutoCompleteItemItemClickedSubject(): PublishSubject<WeatherForecastTinySetupView.Event.OnAutoCompleteItemClicked> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastTinySetupScope
    fun provideAutoCompleteClickSubject(): PublishSubject<AutoCompleteItem> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastTinySetupScope
    fun provideAutoCompleteAdapter(
        clickSubject: PublishSubject<AutoCompleteItem>
    ): AutoCompleteAdapter =
        AutoCompleteAdapter(clickSubject)

    @Provides
    @JvmStatic
    @WeatherForecastTinySetupScope
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    @JvmStatic
    @WeatherForecastTinySetupScope
    fun provideState(): WeatherForecastTinySetupView.State = WeatherForecastTinySetupView.State()
}