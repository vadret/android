package fi.kroon.vadret.forecastwarning

import androidx.compose.runtime.Immutable
import fi.kroon.vadret.forecastwarning.model.WarningItem
import org.jetbrains.compose.resources.StringResource

@Immutable
internal data class WarningDetailState(
    val warning: WarningItem? = null,
    val isLoading: Boolean = true,
    val messageResource: StringResource? = null
)