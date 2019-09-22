package fi.kroon.vadret.presentation.weatherforecast.di

import dagger.Subcomponent
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.presentation.weatherforecast.WeatherForecastAdapter
import fi.kroon.vadret.presentation.weatherforecast.WeatherForecastFragment
import fi.kroon.vadret.presentation.weatherforecast.WeatherForecastView
import fi.kroon.vadret.presentation.weatherforecast.WeatherForecastViewModel
import fi.kroon.vadret.presentation.weatherforecast.autocomplete.AutoCompleteAdapter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Subcomponent(
    modules = [
        WeatherForecastModule::class
    ]
)
@WeatherForecastFeatureScope
interface WeatherForecastComponent {

    fun inject(weatherForecastFragment: WeatherForecastFragment)

    /**
     *  ViewModel
     */
    fun provideWeatherForecastViewModel(): WeatherForecastViewModel

    /**
     *  PublishSubject
     */
    fun provideOnViewInitialisedSubject(): PublishSubject<WeatherForecastView.Event.OnViewInitialised>
    fun provideOnLocationPermissionDeniedSubject(): PublishSubject<WeatherForecastView.Event.OnLocationPermissionDenied>
    fun provideOnLocationPermissionDeniedNeverAskAgainSubject(): PublishSubject<WeatherForecastView.Event.OnLocationPermissionDeniedNeverAskAgain>
    fun provideOnLocationPermissionGrantedSubject(): PublishSubject<WeatherForecastView.Event.OnLocationPermissionGranted>
    fun provideOnProgressBarEffectStarted(): PublishSubject<WeatherForecastView.Event.OnProgressBarEffectStarted>
    fun provideOnProgressBarEffectStopped(): PublishSubject<WeatherForecastView.Event.OnProgressBarEffectStopped>
    fun provideOnAutoCompleteItemClickedSubject(): PublishSubject<AutoCompleteItem>
    fun provideOnSearchViewDismissed(): PublishSubject<WeatherForecastView.Event.OnSearchViewDismissed>
    fun provideOnFailureHandled(): PublishSubject<WeatherForecastView.Event.OnFailureHandled>
    fun provideOnWeatherListDisplayed(): PublishSubject<WeatherForecastView.Event.OnWeatherListDisplayed>
    fun provideOnScrollPositionRestored(): PublishSubject<WeatherForecastView.Event.OnScrollPositionRestored>
    fun provideOnStateParcelUpdated(): PublishSubject<WeatherForecastView.Event.OnStateParcelUpdated>

    /**
     *  Adapter
     */
    fun provideWeatherForecastAdapter(): WeatherForecastAdapter
    fun provideAutoCompleteAdapter(): AutoCompleteAdapter
    fun provideCompositeDisposable(): CompositeDisposable

    @Subcomponent.Builder
    interface Builder {
        fun forecastModule(module: WeatherForecastModule): Builder
        fun build(): WeatherForecastComponent
    }
}