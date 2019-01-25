package fi.kroon.vadret.data.weather.exception

import fi.kroon.vadret.data.exception.Failure

class WeatherForecastFailure {
    class NoWeatherAvailable : Failure.FeatureFailure()
    class NoWeatherAvailableForThisLocation : Failure.FeatureFailure()
    class CachingWeatherForecastDataFailed : Failure.FeatureFailure()
    class LoadingWeatherSettingFailed : Failure.FeatureFailure()
}