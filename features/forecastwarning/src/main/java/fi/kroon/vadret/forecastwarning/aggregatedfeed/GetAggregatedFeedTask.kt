package fi.kroon.vadret.forecastwarning.aggregatedfeed

import fi.kroon.vadret.data.aggregatedfeed.AggregatedFeedRepository
import fi.kroon.vadret.data.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.data.failure.Failure
import io.github.sphrak.either.Either
import javax.inject.Inject

class GetAggregatedFeedTask @Inject constructor(
    private val repo: AggregatedFeedRepository

) {
    suspend operator fun invoke(counties: List<Int>, feeds: List<Int>): Either<Failure, List<AggregatedFeed>> =
        repo(
            counties = counties,
            feeds = feeds
        )
}