package fi.kroon.vadret.domain.feedsourcepreference

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.feedsourcepreference.FeedSourcePreferenceRepository
import fi.kroon.vadret.data.feedsourcepreference.model.FeedSourcePreferenceEntity
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject

class SetFeedSourcePreferenceListTask @Inject constructor(
    private val repo: FeedSourcePreferenceRepository
) {
    operator fun invoke(entityList: List<FeedSourcePreferenceEntity>): Single<Either<Failure, List<Long>>> =
        repo.insert(entityList = entityList)
}