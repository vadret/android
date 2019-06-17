package fi.kroon.vadret.domain.weatherforecastwidget.shared

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.functional.map
import fi.kroon.vadret.data.weatherforecastwidget.local.WeatherForecastWidgetLocalKeyValueDataSource
import fi.kroon.vadret.util.UPDATE_INTERVAL_WIDGET_KEY
import fi.kroon.vadret.util.common.WeatherForecastUtil.getUpdateIntervalInMillis
import io.reactivex.Single
import javax.inject.Inject

class GetWidgetUpdateIntervalMillisKeyValueTask @Inject constructor(
    private val local: WeatherForecastWidgetLocalKeyValueDataSource
) {
    /**
     *  Translates update interval string to its counterpart
     *  representation in millis.
     */
    operator fun invoke(appWidgetId: Int): Single<Either<Failure, Long>> =
        local
            .getString(key = UPDATE_INTERVAL_WIDGET_KEY, appWidgetId = appWidgetId)
            .map { result: Either<Failure, String> ->
                result.map { period: String ->
                    getUpdateIntervalInMillis(period)
                }
            }
}