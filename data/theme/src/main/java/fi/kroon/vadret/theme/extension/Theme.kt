package fi.kroon.vadret.theme.extension

import fi.kroon.vadret.theme.model.Theme

internal const val LIGHT_THEME_VALUE = "Light"
internal const val DARK_THEME_VALUE = "Dark"
internal const val AMOLED_THEME_VALUE = "AMOLED"

/**
 *  The values the old preference screen wrote,
 *  so existing settings keep working.
 */
internal fun Theme.toPreferenceValue(): String =
    when (this) {
        Theme.LIGHT -> LIGHT_THEME_VALUE
        Theme.DARK -> DARK_THEME_VALUE
        Theme.AMOLED -> AMOLED_THEME_VALUE
    }