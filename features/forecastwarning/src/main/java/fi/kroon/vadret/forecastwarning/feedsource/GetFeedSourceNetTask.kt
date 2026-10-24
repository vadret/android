package fi.kroon.vadret.forecastwarning.feedsource

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.feedsource.FeedSourceRepository
import fi.kroon.vadret.data.feedsource.model.FeedSource
import io.reactivex.Single
import se.fimbla.either.Either
import javax.inject.Inject

class GetFeedSourceNetTask @Inject constructor(
    private val repo: FeedSourceRepository
) {
    operator fun invoke(): Single<Either<Failure, List<FeedSource>>> =
        repo()
}