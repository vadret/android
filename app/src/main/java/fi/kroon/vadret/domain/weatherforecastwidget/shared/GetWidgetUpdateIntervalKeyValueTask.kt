package fi.kroon.vadret.domain.weatherforecastwidget.shared

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.weatherforecastwidget.local.WeatherForecastWidgetLocalKeyValueDataSource
import fi.kroon.vadret.utils.UPDATE_INTERVAL_WIDGET_KEY
import io.reactivex.Single
import javax.inject.Inject

class GetWidgetUpdateIntervalKeyValueTask @Inject constructor(
    private val local: WeatherForecastWidgetLocalKeyValueDataSource
) {
    operator fun invoke(appWidgetId: Int): Single<Either<Failure, String>> =
        local
            .getString(key = UPDATE_INTERVAL_WIDGET_KEY, appWidgetId = appWidgetId)
}