package fi.kroon.vadret.forecastwarning

import androidx.compose.runtime.Immutable
import fi.kroon.vadret.forecastwarning.model.WarningItem
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
internal data class WarningStoryState(
    val warnings: ImmutableList<WarningItem> = persistentListOf(),
    val currentIndex: Int = 0,
    val isLoading: Boolean = true,
    val isFinished: Boolean = false
)