package fi.kroon.vadret.domain.feedsourcepreference

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.feedsourcepreference.local.FeedSourcePreferenceDao
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.util.APP_WARNING_FILTER_KEY
import fi.kroon.vadret.util.extension.asRight
import io.reactivex.Single
import javax.inject.Inject

class GetEnabledFeedSourcePreferenceIdsTask @Inject constructor(
    private val dao: FeedSourcePreferenceDao
) {
    operator fun invoke(): Single<Either<Failure, List<Int>>> =
        dao.getAllEnabledIds(usedBy = APP_WARNING_FILTER_KEY)
            .map { ids: List<Int> ->
                ids.asRight()
            }
}