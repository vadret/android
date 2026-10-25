package fi.kroon.vadret.forecastwarning.extension

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import fi.kroon.vadret.aggregatedfeed.model.WarningLevel
import fi.kroon.vadret.core.R

@StringRes
internal fun WarningLevel.labelResourceId(): Int =
    when (this) {
        WarningLevel.MESSAGE -> R.string.warning_level_message
        WarningLevel.YELLOW -> R.string.warning_level_yellow
        WarningLevel.ORANGE -> R.string.warning_level_orange
        WarningLevel.RED -> R.string.warning_level_red
    }

@DrawableRes
internal fun WarningLevel.iconResourceId(): Int =
    when (this) {
        WarningLevel.MESSAGE -> R.drawable.ic_info_filled

        WarningLevel.YELLOW,
        WarningLevel.ORANGE,
        WarningLevel.RED -> R.drawable.ic_warning_filled
    }