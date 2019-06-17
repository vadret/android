package fi.kroon.vadret.domain.feedsourcepreference

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.feedsourcepreference.FeedSourcePreferenceRepository
import fi.kroon.vadret.data.functional.Either
import io.reactivex.Single
import javax.inject.Inject

class DeleteAllFeedSourcePreferenceTask @Inject constructor(
    private val repo: FeedSourcePreferenceRepository
) {
    operator fun invoke(usedBy: String): Single<Either<Failure, Unit>> =
        repo.deleteAllEnabledUsedBy(usedBy = usedBy)
}