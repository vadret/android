package fi.kroon.vadret.data.feedsource

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.feedsource.model.FeedSource
import fi.kroon.vadret.util.FEED_SOURCE_KRISINFORMATION_ID
import fi.kroon.vadret.util.FEED_SOURCE_SMHI_ID
import fi.kroon.vadret.util.extension.asRight
import fi.kroon.vadret.util.extension.asSingle
import io.reactivex.Single
import se.fimbla.either.Either
import javax.inject.Inject

class FeedSourceRepository @Inject constructor() {

    /**
     *  Krisinformation no longer publishes a list of
     *  feed sources, so the sources the warning feed
     *  can query are fixed.
     */
    private val feedSourceList: List<FeedSource> = listOf(
        FeedSource(
            id = FEED_SOURCE_KRISINFORMATION_ID,
            name = "Krisinformation",
            url = "https://www.krisinformation.se",
            description = "Krisinformation.se",
            type = 0,
            headLineRowLimit = 0
        ),
        FeedSource(
            id = FEED_SOURCE_SMHI_ID,
            name = "SMHI",
            url = "https://www.smhi.se",
            description = "SMHI",
            type = 0,
            headLineRowLimit = 0
        )
    )

    operator fun invoke(): Single<Either<Failure, List<FeedSource>>> =
        feedSourceList
            .asRight()
            .asSingle()
}