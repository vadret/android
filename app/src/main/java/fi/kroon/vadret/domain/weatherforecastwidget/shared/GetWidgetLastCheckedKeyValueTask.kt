package fi.kroon.vadret.domain.weatherforecastwidget.shared

import fi.kroon.vadret.data.weatherforecastwidget.local.WeatherForecastWidgetLocalKeyValueDataSource
import fi.kroon.vadret.utils.LAST_CHECKED_WIDGET_KEY
import javax.inject.Inject

class GetWidgetLastCheckedKeyValueTask @Inject constructor(
    private val local: WeatherForecastWidgetLocalKeyValueDataSource
) {
    operator fun invoke(appWidgetId: Int) = local
        .getLong(key = LAST_CHECKED_WIDGET_KEY, appWidgetId = appWidgetId)
}