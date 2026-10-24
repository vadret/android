package fi.kroon.vadret.data.theme.extension

import fi.kroon.vadret.data.theme.model.Theme
import fi.kroon.vadret.util.AMOLED_THEME
import fi.kroon.vadret.util.DARK_THEME
import fi.kroon.vadret.util.LIGHT_THEME

/**
 *  The stored values are the ones the old preference screen wrote,
 *  so existing settings keep working.
 */
internal fun Theme.toPreferenceValue(): String =
    when (this) {
        Theme.LIGHT -> LIGHT_THEME
        Theme.DARK -> DARK_THEME
        Theme.AMOLED -> AMOLED_THEME
    }