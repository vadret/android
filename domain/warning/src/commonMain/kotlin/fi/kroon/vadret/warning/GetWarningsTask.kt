package fi.kroon.vadret.warning

import fi.kroon.vadret.aggregatedfeed.FeedRepository
import fi.kroon.vadret.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.common.Failure
import io.github.sphrak.either.Either

interface GetWarningsTask {

    suspend operator fun invoke(isRefresh: Boolean): Either<Failure, List<AggregatedFeed>>
}

fun GetWarningsTask(feedRepository: FeedRepository): GetWarningsTask = DefaultGetWarningsTask(feedRepository = feedRepository)