package fi.kroon.vadret.domain.feedsourcepreference

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.feedsourcepreference.FeedSourcePreferenceRepository
import fi.kroon.vadret.data.feedsourcepreference.model.FeedSourcePreferenceEntity
import fi.kroon.vadret.data.functional.Either
import io.reactivex.Single
import javax.inject.Inject

class GetFeedSourcePreferenceListTask @Inject constructor(
    private val repo: FeedSourcePreferenceRepository
) {
    operator fun invoke(): Single<Either<Failure, List<FeedSourcePreferenceEntity>>> = repo
        .getAll()
}