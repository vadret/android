package fi.kroon.vadret.theme.extension

import fi.kroon.vadret.theme.model.Theme

/**
 *  Nothing stored, or a retired value such as
 *  "Light (No background)", means the light theme.
 */
internal fun String?.toTheme(): Theme =
    when (this) {
        DARK_THEME_VALUE -> Theme.DARK
        AMOLED_THEME_VALUE -> Theme.AMOLED
        else -> Theme.LIGHT
    }