package fi.kroon.vadret.forecastwarning

import androidx.annotation.StringRes
import fi.kroon.vadret.forecastwarning.model.WarningFilterState
import fi.kroon.vadret.forecastwarning.model.WarningItem

/**
 *  @param isEmpty set when a load succeeded without warnings,
 *  so a failed load does not claim there are none.
 *  @param filter set while the filter sheet is open.
 */
data class WarningState(
    val warnings: List<WarningItem> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isEmpty: Boolean = false,
    val filter: WarningFilterState? = null,
    @StringRes
    val errorMessageResourceId: Int? = null
)