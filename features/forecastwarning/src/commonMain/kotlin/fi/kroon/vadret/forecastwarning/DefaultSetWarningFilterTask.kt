package fi.kroon.vadret.forecastwarning

import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.warningfilter.WarningFilterRepository
import io.github.sphrak.either.Either

internal class DefaultSetWarningFilterTask(
    private val warningFilterRepository: WarningFilterRepository
) : SetWarningFilterTask {

    override suspend fun invoke(
        enabledDistrictIds: Set<Int>,
        enabledFeedSources: Set<FeedSource>
    ): Either<Failure, Unit> =
        warningFilterRepository.setWarningFilter(
            enabledDistrictIds = enabledDistrictIds,
            enabledFeedSources = enabledFeedSources
        )
}