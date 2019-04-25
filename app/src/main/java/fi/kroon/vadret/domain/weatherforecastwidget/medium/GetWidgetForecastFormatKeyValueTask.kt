package fi.kroon.vadret.domain.weatherforecastwidget.medium

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.weatherforecastwidget.local.WeatherForecastWidgetLocalKeyValueDataSource
import fi.kroon.vadret.utils.FORECAST_FORMAT_WIDGET_KEY
import io.reactivex.Single
import javax.inject.Inject

class GetWidgetForecastFormatKeyValueTask @Inject constructor(
    private val local: WeatherForecastWidgetLocalKeyValueDataSource
) {
    operator fun invoke(appWidgetId: Int): Single<Either<Failure, Int>> =
        local
            .getInt(FORECAST_FORMAT_WIDGET_KEY, appWidgetId)
}