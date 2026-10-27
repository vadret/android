package fi.kroon.vadret.forecastwarning.model

import fi.kroon.vadret.warningfilter.model.DistrictOption
import fi.kroon.vadret.warningfilter.model.FeedSourceOption
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.StringResource

internal data class WarningFilterState(
    val feedSources: ImmutableList<FeedSourceOption>,
    val districts: ImmutableList<DistrictOption>,
    val errorMessageResource: StringResource? = null
)