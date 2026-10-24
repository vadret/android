package fi.kroon.vadret.data.theme.extension

import fi.kroon.vadret.data.theme.model.Theme
import fi.kroon.vadret.util.AMOLED_THEME
import fi.kroon.vadret.util.DARK_THEME

/**
 *  Unknown values, such as the retired "Light (No background)",
 *  fall back to the light theme.
 */
internal fun String.toTheme(): Theme =
    when (this) {
        DARK_THEME -> Theme.DARK
        AMOLED_THEME -> Theme.AMOLED
        else -> Theme.LIGHT
    }