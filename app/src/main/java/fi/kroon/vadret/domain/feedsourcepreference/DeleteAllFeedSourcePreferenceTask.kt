package fi.kroon.vadret.domain.feedsourcepreference

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.feedsourcepreference.FeedSourcePreferenceRepository
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject

class DeleteAllFeedSourcePreferenceTask @Inject constructor(
    private val repo: FeedSourcePreferenceRepository
) {
    operator fun invoke(usedBy: String): Single<Either<Failure, Unit>> =
        repo.deleteAllEnabledUsedBy(usedBy = usedBy)
}