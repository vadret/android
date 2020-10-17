package fi.kroon.vadret.presentation.weatherforecastwidget.tiny.setup.di

import dagger.Subcomponent
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.presentation.weatherforecastwidget.shared.AutoCompleteAdapterLegacy
import fi.kroon.vadret.presentation.weatherforecastwidget.tiny.setup.WeatherForecastTinySetup
import fi.kroon.vadret.presentation.weatherforecastwidget.tiny.setup.WeatherForecastTinySetupView
import fi.kroon.vadret.presentation.weatherforecastwidget.tiny.setup.WeatherForecastTinySetupViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Subcomponent(
    modules = [
        WeatherForecastTinySetupModule::class
    ]
)
@WeatherForecastTinySetupScope
interface WeatherForecastTinySetupComponent {

    fun inject(weatherForecastTinySetup: WeatherForecastTinySetup)

    /**
     *  ViewModel
     */
    fun provideWeatherForecastTinySetupViewModel(): WeatherForecastTinySetupViewModel

    /**
     *  CompositeDisposable
     */
    fun provideCompositeDisposable(): CompositeDisposable

    /**
     *  PublishSubject
     */
    fun provideOnSetupInitialised(): PublishSubject<WeatherForecastTinySetupView.Event.OnSetupInitialised>
    fun provideOnConfigurationConfirmed(): PublishSubject<WeatherForecastTinySetupView.Event.OnConfigurationConfirmed>
    fun provideOnCanceledClicked(): PublishSubject<WeatherForecastTinySetupView.Event.OnCanceledClicked>
    fun provideOnLocalitySearchEnabled(): PublishSubject<WeatherForecastTinySetupView.Event.OnLocalitySearchEnabled>
    fun provideOnLocalitySearchDisabled(): PublishSubject<WeatherForecastTinySetupView.Event.OnLocalitySearchDisabled>
    fun provideOnAutoCompleteItemClicked(): PublishSubject<AutoCompleteItem>
    fun provideOnSearchViewDismissed(): PublishSubject<WeatherForecastTinySetupView.Event.OnSearchViewDismissed>
    fun provideOnLocalityTextUpdated(): PublishSubject<WeatherForecastTinySetupView.Event.OnLocalityTextUpdated>
    fun provideOnLocationPermissionDenied(): PublishSubject<WeatherForecastTinySetupView.Event.OnLocationPermissionDenied>

    /**
     *  Adapter
     */
    fun provideAutoCompleteAdapter(): AutoCompleteAdapterLegacy

    @Subcomponent.Builder
    interface Builder {
        fun weatherForecastAppWidgetTinySetupModule(module: WeatherForecastTinySetupModule): Builder
        fun build(): WeatherForecastTinySetupComponent
    }
}