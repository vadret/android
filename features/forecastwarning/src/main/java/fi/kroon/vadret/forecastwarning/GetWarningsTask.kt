package fi.kroon.vadret.forecastwarning

import fi.kroon.vadret.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.common.Failure
import io.github.sphrak.either.Either

internal interface GetWarningsTask {

    /**
     *  The warnings for the stored filter, newest first.
     *  [isRefresh] skips the cached feed.
     */
    suspend operator fun invoke(isRefresh: Boolean): Either<Failure, List<AggregatedFeed>>
}