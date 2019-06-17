package fi.kroon.vadret.domain.weatherforecastwidget.shared

import fi.kroon.vadret.data.weatherforecastwidget.local.WeatherForecastWidgetLocalKeyValueDataSource
import fi.kroon.vadret.util.LAST_CHECKED_WIDGET_KEY
import javax.inject.Inject

class SetWidgetLastCheckedKeyValueTask @Inject constructor(
    private val local: WeatherForecastWidgetLocalKeyValueDataSource
) {
    operator fun invoke(appWidgetId: Int, value: Long) = local
        .putLong(key = LAST_CHECKED_WIDGET_KEY, appWidgetId = appWidgetId, value = value)
}