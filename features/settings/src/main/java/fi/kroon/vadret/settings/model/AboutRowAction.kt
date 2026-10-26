package fi.kroon.vadret.settings.model

import androidx.annotation.StringRes

internal sealed interface AboutRowAction {

    data class OpenUrl(
        @StringRes
        val urlResourceId: Int
    ) : AboutRowAction

    data object ShowChangelog : AboutRowAction
}