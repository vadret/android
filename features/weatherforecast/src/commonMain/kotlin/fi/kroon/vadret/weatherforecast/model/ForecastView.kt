package fi.kroon.vadret.weatherforecast.model

import fi.kroon.vadret.core.resources.Res
import fi.kroon.vadret.core.resources.forecast_days
import fi.kroon.vadret.core.resources.forecast_hours
import fi.kroon.vadret.core.resources.ic_calendar_view_day
import fi.kroon.vadret.core.resources.ic_schedule
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class ForecastView(
    val titleResource: StringResource,
    val iconResource: DrawableResource
) {
    DAYS(
        titleResource = Res.string.forecast_days,
        iconResource = Res.drawable.ic_calendar_view_day
    ),
    HOURS(
        titleResource = Res.string.forecast_hours,
        iconResource = Res.drawable.ic_schedule
    )
}