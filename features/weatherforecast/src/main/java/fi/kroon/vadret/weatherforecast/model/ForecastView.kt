package fi.kroon.vadret.weatherforecast.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import fi.kroon.vadret.core.R

enum class ForecastView(
    @StringRes val titleResourceId: Int,
    @DrawableRes val iconResourceId: Int
) {
    DAYS(
        titleResourceId = R.string.forecast_days,
        iconResourceId = R.drawable.ic_calendar_view_day
    ),
    HOURS(
        titleResourceId = R.string.forecast_hours,
        iconResourceId = R.drawable.ic_schedule
    )
}