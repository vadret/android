package fi.kroon.vadret.domain.weatherforecastwidget.shared

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.weatherforecastwidget.local.WeatherForecastWidgetLocalKeyValueDataSource
import fi.kroon.vadret.util.COUNTY_WIDGET_KEY
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject

class SetWidgetCountyKeyValueTask @Inject constructor(
    private val local: WeatherForecastWidgetLocalKeyValueDataSource
) {
    operator fun invoke(appWidgetId: Int, value: String): Single<Either<Failure, Unit>> =
        local.putString(key = COUNTY_WIDGET_KEY, appWidgetId = appWidgetId, value = value)
}