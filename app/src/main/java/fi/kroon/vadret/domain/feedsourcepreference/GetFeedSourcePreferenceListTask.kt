package fi.kroon.vadret.domain.feedsourcepreference

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.feedsourcepreference.FeedSourcePreferenceRepository
import fi.kroon.vadret.data.feedsourcepreference.model.FeedSourcePreferenceEntity
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject

class GetFeedSourcePreferenceListTask @Inject constructor(
    private val repo: FeedSourcePreferenceRepository
) {
    operator fun invoke(): Single<Either<Failure, List<FeedSourcePreferenceEntity>>> = repo
        .getAll()
}