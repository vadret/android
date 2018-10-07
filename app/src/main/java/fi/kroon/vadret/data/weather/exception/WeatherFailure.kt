package fi.kroon.vadret.data.weather.exception

import fi.kroon.vadret.data.exception.Failure

class WeatherFailure {
    class NoWeatherAvailable : Failure.FeatureFailure()
    class NoWeatherAvailableForThisLocation : Failure.FeatureFailure()
}