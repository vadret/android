package fi.kroon.vadret.forecastwarning.feedsourcepreference

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.feedsourcepreference.FeedSourcePreferenceRepository
import io.reactivex.Single
import se.fimbla.either.Either
import javax.inject.Inject

class DeleteAllFeedSourcePreferenceTask @Inject constructor(
    private val repo: FeedSourcePreferenceRepository
) {
    operator fun invoke(usedBy: String): Single<Either<Failure, Unit>> =
        repo.deleteAllEnabledUsedBy(usedBy = usedBy)
}