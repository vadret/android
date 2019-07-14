package fi.kroon.vadret.data.feedsource

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.exception.IErrorHandler
import fi.kroon.vadret.data.feedsource.model.FeedSource
import fi.kroon.vadret.data.feedsource.net.FeedSourceNetDataSource
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.di.scope.CoreApplicationScope
import fi.kroon.vadret.util.NetworkHandler
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.reactivex.Single
import retrofit2.Response
import javax.inject.Inject

@CoreApplicationScope
class FeedSourceRepository @Inject constructor(
    private val networkHandler: NetworkHandler,
    private val feedSourceNetDataSource: FeedSourceNetDataSource,
    private val errorHandler: ErrorHandler
) : IErrorHandler by errorHandler {
    /**
     *  When [response.body] is null [NetworkResponseEmpty] is
     *  returned.
     */
    operator fun invoke(): Single<Either<Failure, List<FeedSource>>> =
        when (networkHandler.isConnected) {
            true -> {
                feedSourceNetDataSource()
                    .map { response: Response<List<FeedSource>> ->
                        response
                            .body()
                            ?.asRight()
                            ?: Failure
                                .NetworkResponseEmpty
                                .asLeft()
                    }
            }
            false -> getNetworkOfflineError()
        }
}