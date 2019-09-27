package fi.kroon.vadret.domain.weatherforecastwidget.shared

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.weatherforecastwidget.local.WeatherForecastWidgetLocalKeyValueDataSource
import fi.kroon.vadret.util.INITIALISED_STATUS_WIDGET_KEY
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject

class SetWidgetInitialisedKeyValueTask @Inject constructor(
    private val local: WeatherForecastWidgetLocalKeyValueDataSource
) {
    operator fun invoke(appWidgetId: Int, value: Boolean): Single<Either<Failure, Unit>> =
        local.putBoolean(
            key = INITIALISED_STATUS_WIDGET_KEY,
            appWidgetId = appWidgetId,
            value = value
        )
}