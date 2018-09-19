package fi.kroon.vadret.data.weather

import fi.kroon.vadret.data.exception.Failure

class WeatherFailure {
    class NoWeatherAvailable : Failure.FeatureFailure()
}