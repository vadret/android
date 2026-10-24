package fi.kroon.vadret.weatherforecast.di

import dagger.Binds
import dagger.Module
import fi.kroon.vadret.weatherforecast.DefaultGetLocationModeTask
import fi.kroon.vadret.weatherforecast.DefaultGetWeatherForecastTask
import fi.kroon.vadret.weatherforecast.DefaultSearchPlacesTask
import fi.kroon.vadret.weatherforecast.DefaultSelectPlaceTask
import fi.kroon.vadret.weatherforecast.DefaultSetLocationModeTask
import fi.kroon.vadret.weatherforecast.GetLocationModeTask
import fi.kroon.vadret.weatherforecast.GetWeatherForecastTask
import fi.kroon.vadret.weatherforecast.SearchPlacesTask
import fi.kroon.vadret.weatherforecast.SelectPlaceTask
import fi.kroon.vadret.weatherforecast.SetLocationModeTask

@Module
internal interface WeatherForecastModule {

    @Binds
    fun bindGetLocationModeTask(defaultGetLocationModeTask: DefaultGetLocationModeTask): GetLocationModeTask

    @Binds
    fun bindGetWeatherForecastTask(defaultGetWeatherForecastTask: DefaultGetWeatherForecastTask): GetWeatherForecastTask

    @Binds
    fun bindSearchPlacesTask(defaultSearchPlacesTask: DefaultSearchPlacesTask): SearchPlacesTask

    @Binds
    fun bindSelectPlaceTask(defaultSelectPlaceTask: DefaultSelectPlaceTask): SelectPlaceTask

    @Binds
    fun bindSetLocationModeTask(defaultSetLocationModeTask: DefaultSetLocationModeTask): SetLocationModeTask
}