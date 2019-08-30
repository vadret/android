package fi.kroon.vadret.domain.aggregatedfeed

import fi.kroon.vadret.data.aggregatedfeed.AggregatedFeedRepository
import fi.kroon.vadret.data.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.data.exception.Failure
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject

class GetAggregatedFeedTask @Inject constructor(
    private val repo: AggregatedFeedRepository

) {
    operator fun invoke(counties: List<Int>, feeds: List<Int>): Single<Either<Failure, List<AggregatedFeed>>> =
        repo(
            counties = counties.joinToString(","),
            feeds = feeds.joinToString(",")
        )
}