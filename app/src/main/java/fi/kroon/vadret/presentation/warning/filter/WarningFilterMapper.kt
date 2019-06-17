package fi.kroon.vadret.presentation.warning.filter

import fi.kroon.vadret.R
import fi.kroon.vadret.data.district.model.DistrictOptionEntity
import fi.kroon.vadret.data.feedsource.model.FeedSourceOptionEntity
import fi.kroon.vadret.presentation.warning.filter.model.IFilterable
import fi.kroon.vadret.presentation.warning.filter.model.TitleModel

object WarningFilterMapper {

    operator fun invoke(
        districtOptionEntityList: List<DistrictOptionEntity>,
        feedSourceOptionEntityList: List<FeedSourceOptionEntity>
    ): List<IFilterable> {

        val districtTitle = TitleModel(R.string.filter_district)
        val feedSourceTitle = TitleModel(R.string.filter_feed_source)
        val filterableList: MutableList<IFilterable> = mutableListOf()

        filterableList.add(feedSourceTitle)
        filterableList.addAll(feedSourceOptionEntityList)

        filterableList.add(districtTitle)
        filterableList.addAll(districtOptionEntityList)

        return filterableList
    }
}