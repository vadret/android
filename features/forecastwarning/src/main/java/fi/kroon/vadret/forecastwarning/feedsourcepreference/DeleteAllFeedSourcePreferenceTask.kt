package fi.kroon.vadret.forecastwarning.feedsourcepreference

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.feedsourcepreference.FeedSourcePreferenceRepository
import io.github.sphrak.either.Either
import javax.inject.Inject

class DeleteAllFeedSourcePreferenceTask @Inject constructor(
    private val repo: FeedSourcePreferenceRepository
) {
    suspend operator fun invoke(usedBy: String): Either<Failure, Unit> =
        repo.deleteAllEnabledUsedBy(usedBy = usedBy)
}