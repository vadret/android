package fi.kroon.vadret.weatherforecast.di

import fi.kroon.vadret.weatherforecast.DefaultGetLocationModeTask
import fi.kroon.vadret.weatherforecast.DefaultGetWeatherForecastTask
import fi.kroon.vadret.weatherforecast.DefaultObserveForecastPreferencesTask
import fi.kroon.vadret.weatherforecast.DefaultObserveLocationChoicesTask
import fi.kroon.vadret.weatherforecast.DefaultRemoveFavoriteTask
import fi.kroon.vadret.weatherforecast.DefaultSearchPlacesTask
import fi.kroon.vadret.weatherforecast.DefaultSelectFavoriteTask
import fi.kroon.vadret.weatherforecast.DefaultSelectPlaceTask
import fi.kroon.vadret.weatherforecast.DefaultSetLocationModeTask
import fi.kroon.vadret.weatherforecast.GetLocationModeTask
import fi.kroon.vadret.weatherforecast.GetWeatherForecastTask
import fi.kroon.vadret.weatherforecast.ObserveForecastPreferencesTask
import fi.kroon.vadret.weatherforecast.ObserveLocationChoicesTask
import fi.kroon.vadret.weatherforecast.RemoveFavoriteTask
import fi.kroon.vadret.weatherforecast.SearchPlacesTask
import fi.kroon.vadret.weatherforecast.SelectFavoriteTask
import fi.kroon.vadret.weatherforecast.SelectPlaceTask
import fi.kroon.vadret.weatherforecast.SetLocationModeTask
import fi.kroon.vadret.weatherforecast.WeatherForecastViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val weatherForecastModule: Module = module {
    factoryOf(::DefaultGetLocationModeTask) { bind<GetLocationModeTask>() }
    factoryOf(::DefaultGetWeatherForecastTask) { bind<GetWeatherForecastTask>() }
    factoryOf(::DefaultObserveForecastPreferencesTask) { bind<ObserveForecastPreferencesTask>() }
    factoryOf(::DefaultObserveLocationChoicesTask) { bind<ObserveLocationChoicesTask>() }
    factoryOf(::DefaultRemoveFavoriteTask) { bind<RemoveFavoriteTask>() }
    factoryOf(::DefaultSearchPlacesTask) { bind<SearchPlacesTask>() }
    factoryOf(::DefaultSelectFavoriteTask) { bind<SelectFavoriteTask>() }
    factoryOf(::DefaultSelectPlaceTask) { bind<SelectPlaceTask>() }
    factoryOf(::DefaultSetLocationModeTask) { bind<SetLocationModeTask>() }
    factoryOf(::WeatherForecastViewModel)
}