package fi.kroon.vadret.forecastwarning.model

import androidx.annotation.StringRes
import fi.kroon.vadret.data.district.model.DistrictOptionEntity
import fi.kroon.vadret.data.feedsource.model.FeedSourceOptionEntity

/**
 *  The filter being edited. Nothing is stored until it is applied.
 */
data class WarningFilter(
    val feedSourceOptions: List<FeedSourceOptionEntity>,
    val districtOptions: List<DistrictOptionEntity>,
    @StringRes
    val errorMessageResourceId: Int? = null
)