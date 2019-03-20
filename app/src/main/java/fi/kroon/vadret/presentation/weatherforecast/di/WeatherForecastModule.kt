package fi.kroon.vadret.presentation.weatherforecast.di

import dagger.Module
import dagger.Provides
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.presentation.weatherforecast.WeatherForecastView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Module
object WeatherForecastModule {

    @Provides
    @JvmStatic
    @WeatherForecastFeatureScope
    fun provideOnStateParcelUpdated(): PublishSubject<WeatherForecastView.Event.OnStateParcelUpdated> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastFeatureScope
    fun provideOnScrollPositionRestoredSubject(): PublishSubject<WeatherForecastView.Event.OnScrollPositionRestored> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastFeatureScope
    fun provideOnWeatherListVisibleSubject(): PublishSubject<WeatherForecastView.Event.OnWeatherListDisplayed> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastFeatureScope
    fun provideOnPostLoadWeatherForecastSubject(): PublishSubject<WeatherForecastView.Event.OnProgressBarEffectStarted> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastFeatureScope
    fun provideOnStopProgressBarEffectSubject(): PublishSubject<WeatherForecastView.Event.OnProgressBarEffectStopped> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastFeatureScope
    fun provideOnFailurePropagatedSubject(): PublishSubject<WeatherForecastView.Event.OnFailureHandled> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastFeatureScope
    fun onLocationPermissionDeniedNeverAskAgainSubject(): PublishSubject<WeatherForecastView.Event.OnLocationPermissionDeniedNeverAskAgain> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastFeatureScope
    fun provideOnSearchViewDismissedSubject(): PublishSubject<WeatherForecastView.Event.OnSearchViewDismissed> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastFeatureScope
    fun provideAutoCompleteClickSubject(): PublishSubject<AutoCompleteItem> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastFeatureScope
    fun provideViewState(): WeatherForecastView.State = WeatherForecastView.State()

    @Provides
    @JvmStatic
    @WeatherForecastFeatureScope
    fun provideOnAutoCompleteItemClickedSubject(): PublishSubject<WeatherForecastView.Event.OnAutoCompleteItemClicked> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastFeatureScope
    fun provideOnInitEventSubject(): PublishSubject<WeatherForecastView.Event.OnViewInitialised> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastFeatureScope
    fun provideOnLocationPermissionDeniedEventSubject(): PublishSubject<WeatherForecastView.Event.OnLocationPermissionDenied> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastFeatureScope
    fun provideOnLocationPermissionGrantedEventSubject(): PublishSubject<WeatherForecastView.Event.OnLocationPermissionGranted> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastFeatureScope
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()
}