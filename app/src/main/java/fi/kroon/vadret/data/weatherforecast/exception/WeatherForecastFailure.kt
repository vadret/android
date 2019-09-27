package fi.kroon.vadret.data.weatherforecast.exception

import fi.kroon.vadret.data.failure.Failure

class WeatherForecastFailure {
    object NoWeatherAvailable : Failure.FeatureFailure()
    object NoWeatherAvailableForThisLocation : Failure.FeatureFailure()
    object CachingWeatherForecastDataFailed : Failure.FeatureFailure()
}