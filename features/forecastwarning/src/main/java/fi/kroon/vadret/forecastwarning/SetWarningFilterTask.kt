package fi.kroon.vadret.forecastwarning

import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.common.Failure
import io.github.sphrak.either.Either

internal interface SetWarningFilterTask {
    suspend operator fun invoke(
        enabledDistrictIds: Set<Int>,
        enabledFeedSources: Set<FeedSource>
    ): Either<Failure, Unit>
}