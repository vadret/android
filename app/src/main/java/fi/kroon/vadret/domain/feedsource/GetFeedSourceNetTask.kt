package fi.kroon.vadret.domain.feedsource

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.feedsource.FeedSourceRepository
import fi.kroon.vadret.data.feedsource.model.FeedSource
import fi.kroon.vadret.data.functional.Either
import io.reactivex.Single
import javax.inject.Inject

class GetFeedSourceNetTask @Inject constructor(
    private val repo: FeedSourceRepository
) {
    operator fun invoke(): Single<Either<Failure, List<FeedSource>>> =
        repo()
}