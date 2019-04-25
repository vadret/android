package fi.kroon.vadret.domain.weatherforecastwidget.shared

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.weatherforecastwidget.local.WeatherForecastWidgetLocalKeyValueDataSource
import fi.kroon.vadret.utils.AUTOMATIC_LOCATION_MODE_WIDGET_KEY
import io.reactivex.Single
import javax.inject.Inject

class SetWidgetLocationModeKeyValueTask @Inject constructor(
    private val local: WeatherForecastWidgetLocalKeyValueDataSource
) {
    operator fun invoke(appWidgetId: Int, value: Boolean): Single<Either<Failure, Unit>> =
        local
            .putBoolean(
                key = AUTOMATIC_LOCATION_MODE_WIDGET_KEY,
                appWidgetId = appWidgetId,
                value = value
            )
}