package fi.kroon.vadret.forecastwarning.model

import androidx.annotation.StringRes
import fi.kroon.vadret.warningfilter.model.DistrictOption
import fi.kroon.vadret.warningfilter.model.FeedSourceOption
import kotlinx.collections.immutable.ImmutableList

internal data class WarningFilterState(
    val feedSources: ImmutableList<FeedSourceOption>,
    val districts: ImmutableList<DistrictOption>,
    @StringRes
    val errorMessageResourceId: Int? = null
)