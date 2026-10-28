package fi.kroon.vadret.warning

import fi.kroon.vadret.aggregatedfeed.FeedRepository
import fi.kroon.vadret.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.warning.model.WarningPlace
import io.github.sphrak.either.Either
import kotlinx.coroutines.flow.Flow

interface ObserveUnseenWarningsTask {

    operator fun invoke(
        warningPlace: WarningPlace,
        isRefresh: Boolean
    ): Flow<Either<Failure, List<AggregatedFeed>>>
}

fun ObserveUnseenWarningsTask(feedRepository: FeedRepository): ObserveUnseenWarningsTask = DefaultObserveUnseenWarningsTask(feedRepository = feedRepository)