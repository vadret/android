package fi.kroon.vadret.forecastwarning

import fi.kroon.vadret.aggregatedfeed.FeedRepository
import fi.kroon.vadret.common.Failure
import io.github.sphrak.either.Either

internal class DefaultMarkWarningSeenTask(
    private val feedRepository: FeedRepository
) : MarkWarningSeenTask {

    override suspend fun invoke(warningIdentifier: String): Either<Failure, Unit> = feedRepository.markSeen(warningIdentifier = warningIdentifier)
}