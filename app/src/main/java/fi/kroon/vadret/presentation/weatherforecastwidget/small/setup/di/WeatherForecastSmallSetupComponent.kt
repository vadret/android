package fi.kroon.vadret.presentation.weatherforecastwidget.small.setup.di

import dagger.Subcomponent
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.presentation.weatherforecast.autocomplete.AutoCompleteAdapter
import fi.kroon.vadret.presentation.weatherforecastwidget.small.setup.WeatherForecastSmallSetup
import fi.kroon.vadret.presentation.weatherforecastwidget.small.setup.WeatherForecastSmallSetupView
import fi.kroon.vadret.presentation.weatherforecastwidget.small.setup.WeatherForecastSmallSetupViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Subcomponent(
    modules = [
        WeatherForecastSmallSetupModule::class
    ]
)
@WeatherForecastSmallSetupScope
interface WeatherForecastSmallSetupComponent {

    fun inject(weatherForecastSmallSetup: WeatherForecastSmallSetup)

    /**
     *  ViewModel
     */
    fun provideWeatherForecastSmallSetupViewModel(): WeatherForecastSmallSetupViewModel

    /**
     *  PublishSubject
     */
    fun provideOnSetupInitialised(): PublishSubject<WeatherForecastSmallSetupView.Event.OnSetupInitialised>
    fun provideOnConfigurationConfirmed(): PublishSubject<WeatherForecastSmallSetupView.Event.OnConfigurationConfirmed>
    fun provideOnCanceledClicked(): PublishSubject<WeatherForecastSmallSetupView.Event.OnCanceledClicked>
    fun provideOnLocalitySearchEnabled(): PublishSubject<WeatherForecastSmallSetupView.Event.OnLocalitySearchEnabled>
    fun provideOnLocalitySearchDisabled(): PublishSubject<WeatherForecastSmallSetupView.Event.OnLocalitySearchDisabled>
    fun provideOnAutoCompleteItemClicked(): PublishSubject<AutoCompleteItem>
    fun provideOnSearchViewDismissed(): PublishSubject<WeatherForecastSmallSetupView.Event.OnSearchViewDismissed>
    fun provideOnLocalityTextUpdated(): PublishSubject<WeatherForecastSmallSetupView.Event.OnLocalityTextUpdated>
    fun provideOnLocationPermissionDenied(): PublishSubject<WeatherForecastSmallSetupView.Event.OnLocationPermissionDenied>

    /**
     *  Adapter
     */
    fun provideAutoCompleteAdapter(): AutoCompleteAdapter

    /**
     *  CompositeDisposable
     */
    fun provideCompositeDisposable(): CompositeDisposable

    @Subcomponent.Builder
    interface Builder {
        fun weatherForecastAppWidgetSmallSetupModule(module: WeatherForecastSmallSetupModule): Builder
        fun build(): WeatherForecastSmallSetupComponent
    }
}