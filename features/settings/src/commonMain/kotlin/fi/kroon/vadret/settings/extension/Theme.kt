package fi.kroon.vadret.settings.extension

import fi.kroon.vadret.core.resources.Res
import fi.kroon.vadret.core.resources.amoled_key
import fi.kroon.vadret.core.resources.dark_key
import fi.kroon.vadret.core.resources.light_key
import fi.kroon.vadret.theme.model.Theme
import org.jetbrains.compose.resources.StringResource

internal fun Theme.labelResource(): StringResource =
    when (this) {
        Theme.LIGHT -> Res.string.light_key
        Theme.DARK -> Res.string.dark_key
        Theme.AMOLED -> Res.string.amoled_key
    }