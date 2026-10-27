package fi.kroon.vadret.settings.model

import org.jetbrains.compose.resources.StringResource

internal sealed interface AboutRowAction {

    data class OpenUrl(
        val urlResource: StringResource
    ) : AboutRowAction

    data object ShowChangelog : AboutRowAction
}