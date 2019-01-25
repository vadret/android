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
    @WeatherForecastScope
    fun provideOnStateParcelUpdated(): PublishSubject<WeatherForecastView.Event.OnStateParcelUpdated> = PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastScope
    fun provideOnScrollPositionRestoredSubject(): PublishSubject<WeatherForecastView.Event.OnScrollPositionRestored> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastScope
    fun provideOnWeatherListVisibleSubject(): PublishSubject<WeatherForecastView.Event.OnWeatherListDisplayed> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastScope
    fun provideOnPreLoadWeatherForecastSubject(): PublishSubject<WeatherForecastView.Event.OnShimmerEffectStarted> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastScope
    fun provideOnPostLoadWeatherForecastSubject(): PublishSubject<WeatherForecastView.Event.OnProgressBarEffectStarted> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastScope
    fun provideOnStopShimmerEffectSubject(): PublishSubject<WeatherForecastView.Event.OnShimmerEffectStopped> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastScope
    fun provideOnStopProgressBarEffectSubject(): PublishSubject<WeatherForecastView.Event.OnProgressBarEffectStopped> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastScope
    fun provideOnFailurePropagatedSubject(): PublishSubject<WeatherForecastView.Event.OnFailureHandled> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastScope
    fun onLocationPermissionDeniedNeverAskAgainSubject(): PublishSubject<WeatherForecastView.Event.OnLocationPermissionDeniedNeverAskAgain> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastScope
    fun provideOnSearchViewDismissedSubject(): PublishSubject<WeatherForecastView.Event.OnSearchViewDismissed> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastScope
    fun provideAutoCompleteClickSubject(): PublishSubject<AutoCompleteItem> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastScope
    fun provideViewState(): WeatherForecastView.State = WeatherForecastView.State()

    @Provides
    @JvmStatic
    @WeatherForecastScope
    fun provideOnAutoCompleteItemClickedSubject(): PublishSubject<WeatherForecastView.Event.OnAutoCompleteItemClicked> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastScope
    fun provideOnInitEventSubject(): PublishSubject<WeatherForecastView.Event.OnViewInitialised> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastScope
    fun provideOnLocationPermissionDeniedEventSubject(): PublishSubject<WeatherForecastView.Event.OnLocationPermissionDenied> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastScope
    fun provideOnLocationPermissionGrantedEventSubject(): PublishSubject<WeatherForecastView.Event.OnLocationPermissionGranted> =
        PublishSubject.create()

    @Provides
    @JvmStatic
    @WeatherForecastScope
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()
}