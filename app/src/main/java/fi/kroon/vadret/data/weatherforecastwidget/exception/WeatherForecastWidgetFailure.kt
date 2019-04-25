package fi.kroon.vadret.data.weatherforecastwidget.exception

import fi.kroon.vadret.data.exception.Failure

class WeatherForecastWidgetFailure {
    object NoLocalitySelected : Failure.FeatureFailure()
}