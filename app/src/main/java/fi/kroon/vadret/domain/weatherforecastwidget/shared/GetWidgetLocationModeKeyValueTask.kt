package fi.kroon.vadret.domain.weatherforecastwidget.shared

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.weatherforecastwidget.local.WeatherForecastWidgetLocalKeyValueDataSource
import fi.kroon.vadret.util.AUTOMATIC_LOCATION_MODE_WIDGET_KEY
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject

class GetWidgetLocationModeKeyValueTask @Inject constructor(
    private val localWeatherForecast: WeatherForecastWidgetLocalKeyValueDataSource
) {
    /**
     *  Retrieves the widgets location acquisition policy.
     */
    operator fun invoke(appWidgetId: Int): Single<Either<Failure, Boolean>> = localWeatherForecast
        .getBoolean(key = AUTOMATIC_LOCATION_MODE_WIDGET_KEY, appWidgetId = appWidgetId)
}