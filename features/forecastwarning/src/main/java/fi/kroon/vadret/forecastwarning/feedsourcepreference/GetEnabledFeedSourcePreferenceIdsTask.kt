package fi.kroon.vadret.forecastwarning.feedsourcepreference

import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.feedsourcepreference.local.FeedSourcePreferenceDao
import fi.kroon.vadret.util.APP_WARNING_FILTER_KEY
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import javax.inject.Inject

class GetEnabledFeedSourcePreferenceIdsTask @Inject constructor(
    private val dao: FeedSourcePreferenceDao
) {
    suspend operator fun invoke(): Either<Failure, List<Int>> =
        dao.getAllEnabledIds(usedBy = APP_WARNING_FILTER_KEY)
            .let { ids: List<Int> ->
                ids.asRight()
            }
}