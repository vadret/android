package fi.kroon.vadret.forecastwarning.feedsourcepreference

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.feedsourcepreference.FeedSourcePreferenceRepository
import fi.kroon.vadret.data.feedsourcepreference.model.FeedSourcePreferenceEntity
import io.github.sphrak.either.Either
import javax.inject.Inject

class GetFeedSourcePreferenceListTask @Inject constructor(
    private val repo: FeedSourcePreferenceRepository
) {
    suspend operator fun invoke(): Either<Failure, List<FeedSourcePreferenceEntity>> = repo
        .getAll()
}