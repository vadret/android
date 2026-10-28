package fi.kroon.vadret.forecastwarning

import androidx.compose.runtime.Immutable
import fi.kroon.vadret.forecastwarning.model.WarningItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.StringResource

@Immutable
internal data class WarningState(
    val warnings: ImmutableList<WarningItem> = persistentListOf(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isEmpty: Boolean = false,
    val errorMessageResource: StringResource? = null
)