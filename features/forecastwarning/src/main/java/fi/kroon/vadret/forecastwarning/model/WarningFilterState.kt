package fi.kroon.vadret.forecastwarning.model

import androidx.annotation.StringRes
import fi.kroon.vadret.warningfilter.model.DistrictOption
import fi.kroon.vadret.warningfilter.model.FeedSourceOption

/**
 *  The filter being edited. Nothing is stored until it is applied.
 */
data class WarningFilterState(
    val feedSources: List<FeedSourceOption>,
    val districts: List<DistrictOption>,
    @StringRes
    val errorMessageResourceId: Int? = null
)