package fi.kroon.vadret.warning

import fi.kroon.vadret.aggregatedfeed.FeedRepository
import fi.kroon.vadret.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.common.Failure
import io.github.sphrak.either.Either

internal class DefaultGetWarningsTask(
    private val feedRepository: FeedRepository
) : GetWarningsTask {

    override suspend fun invoke(isRefresh: Boolean): Either<Failure, List<AggregatedFeed>> = feedRepository.getAggregatedFeed(isRefresh = isRefresh)
}