package fi.kroon.vadret.settings.extension

import androidx.annotation.StringRes
import fi.kroon.vadret.core.R
import fi.kroon.vadret.theme.model.Theme

@StringRes
internal fun Theme.labelResourceId(): Int =
    when (this) {
        Theme.LIGHT -> R.string.light_key
        Theme.DARK -> R.string.dark_key
        Theme.AMOLED -> R.string.amoled_key
    }