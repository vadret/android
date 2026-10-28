package fi.kroon.vadret.ui.extension

import fi.kroon.vadret.aggregatedfeed.model.WarningLevel
import fi.kroon.vadret.core.resources.Res
import fi.kroon.vadret.core.resources.warning_level_message
import fi.kroon.vadret.core.resources.warning_level_orange
import fi.kroon.vadret.core.resources.warning_level_red
import fi.kroon.vadret.core.resources.warning_level_yellow
import org.jetbrains.compose.resources.StringResource

fun WarningLevel.labelResource(): StringResource =
    when (this) {
        WarningLevel.MESSAGE -> Res.string.warning_level_message
        WarningLevel.YELLOW -> Res.string.warning_level_yellow
        WarningLevel.ORANGE -> Res.string.warning_level_orange
        WarningLevel.RED -> Res.string.warning_level_red
    }