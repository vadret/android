package fi.kroon.vadret.domain.weatherforecastwidget.shared

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.weatherforecastwidget.local.WeatherForecastWidgetLocalKeyValueDataSource
import fi.kroon.vadret.utils.MUNICIPALITY_WIDGET_KEY
import io.reactivex.Single
import javax.inject.Inject

class SetWidgetMunicipalityKeyValueTask @Inject constructor(
    private val local: WeatherForecastWidgetLocalKeyValueDataSource
) {
    operator fun invoke(appWidgetId: Int, value: String): Single<Either<Failure, Unit>> =
        local
            .putString(
                key = MUNICIPALITY_WIDGET_KEY,
                appWidgetId = appWidgetId,
                value = value
            )
}