package fi.kroon.vadret.presentation.weatherforecastwidget.medium.setup.di

import dagger.Subcomponent
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.setup.WeatherForecastMediumSetup
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.setup.WeatherForecastMediumSetupView
import fi.kroon.vadret.presentation.weatherforecastwidget.medium.setup.WeatherForecastMediumSetupViewModel
import fi.kroon.vadret.presentation.weatherforecastwidget.shared.AutoCompleteAdapterLegacy
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Subcomponent(
    modules = [
        WeatherForecastMediumSetupModule::class
    ]
)
@WeatherForecastMediumSetupScope
interface WeatherForecastMediumSetupComponent {

    fun inject(weatherForecastMediumSetup: WeatherForecastMediumSetup)

    /**
     *  ViewModel
     */
    fun provideWeatherForecastMediumSetupViewModel(): WeatherForecastMediumSetupViewModel

    /**
     *  CompositeDisposable
     */
    fun provideCompositeDisposable(): CompositeDisposable

    /**
     *  PublishSubject
     */
    fun provideOnSetupInitialised(): PublishSubject<WeatherForecastMediumSetupView.Event.OnSetupInitialised>

    fun provideOnConfigurationConfirmed(): PublishSubject<WeatherForecastMediumSetupView.Event.OnConfigurationConfirmed>
    fun provideOnCanceledClicked(): PublishSubject<WeatherForecastMediumSetupView.Event.OnCanceledClicked>
    fun provideOnLocalitySearchEnabled(): PublishSubject<WeatherForecastMediumSetupView.Event.OnLocalitySearchEnabled>
    fun provideOnLocalitySearchDisabled(): PublishSubject<WeatherForecastMediumSetupView.Event.OnLocalitySearchDisabled>
    fun provideOnAutoCompleteItemClickedSubject(): PublishSubject<AutoCompleteItem>
    fun provideOnSearchViewDismissed(): PublishSubject<WeatherForecastMediumSetupView.Event.OnSearchViewDismissed>
    fun provideOnLocalityTextUpdated(): PublishSubject<WeatherForecastMediumSetupView.Event.OnLocalityTextUpdated>
    fun provideOnLocationPermissionDenied(): PublishSubject<WeatherForecastMediumSetupView.Event.OnLocationPermissionDenied>

    /**
     *  Adapter
     */
    fun provideAutoCompleteAdapter(): AutoCompleteAdapterLegacy

    @Subcomponent.Builder
    interface Builder {
        fun weatherForecastAppWidgetMediumSetupModule(module: WeatherForecastMediumSetupModule): Builder
        fun build(): WeatherForecastMediumSetupComponent
    }
}