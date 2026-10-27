package fi.kroon.vadret.warning

import fi.kroon.vadret.aggregatedfeed.AggregatedFeedRepository
import fi.kroon.vadret.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.warningfilter.WarningFilterRepository
import io.github.sphrak.either.Either

interface GetWarningsTask {

    suspend operator fun invoke(isRefresh: Boolean): Either<Failure, List<AggregatedFeed>>
}

fun GetWarningsTask(
    warningFilterRepository: WarningFilterRepository,
    aggregatedFeedRepository: AggregatedFeedRepository
): GetWarningsTask =
    DefaultGetWarningsTask(
        warningFilterRepository = warningFilterRepository,
        aggregatedFeedRepository = aggregatedFeedRepository
    )