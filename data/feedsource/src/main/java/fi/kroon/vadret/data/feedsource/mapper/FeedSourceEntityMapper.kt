package fi.kroon.vadret.data.feedsource.mapper

import fi.kroon.vadret.data.feedsource.model.FeedSource
import fi.kroon.vadret.data.feedsource.model.FeedSourceEntity
import kotlin.time.Clock

object FeedSourceEntityMapper {

    /**
     *  Transforms [FeedSource] into it's database
     *  representation [FeedSourceEntity].
     */
    operator fun invoke(feedSourceList: List<FeedSource>): List<FeedSourceEntity> =
        feedSourceList.map { feedSource: FeedSource ->
            with(feedSource) {
                FeedSourceEntity(
                    id = id,
                    description = description,
                    createdAtEpochMillis = Clock.System.now().toEpochMilliseconds(),
                    headLineRowLimit = headLineRowLimit,
                    name = name,
                    type = type,
                    url = url
                )
            }
        }
}