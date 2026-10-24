package fi.kroon.vadret.theme.extension

import fi.kroon.vadret.theme.model.Theme

internal fun String?.toTheme(): Theme =
    Theme.entries.firstOrNull { theme: Theme -> theme.name == this } ?: Theme.LIGHT