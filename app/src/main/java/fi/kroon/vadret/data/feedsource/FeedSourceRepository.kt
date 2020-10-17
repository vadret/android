package fi.kroon.vadret.data.feedsource

import dagger.Lazy
import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.exception.ExceptionHandler
import fi.kroon.vadret.data.exception.IErrorHandler
import fi.kroon.vadret.data.exception.IExceptionHandler
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.feedsource.model.FeedSource
import fi.kroon.vadret.data.feedsource.net.FeedSourceNetDataSource
import fi.kroon.vadret.util.NetworkHandler
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import io.reactivex.Single
import retrofit2.Response
import javax.inject.Inject

class FeedSourceRepository @Inject constructor(
    private val networkHandler: NetworkHandler,
    private val feedSourceNetDataSource: Lazy<FeedSourceNetDataSource>,
    private val errorHandler: ErrorHandler,
    private val exceptionHandler: ExceptionHandler
) : IErrorHandler by errorHandler, IExceptionHandler<Failure> by exceptionHandler {
    /**
     *  When [response.body] is null [NetworkResponseEmpty] is
     *  returned.
     */
    operator fun invoke(): Single<Either<Failure, List<FeedSource>>> =
        when (networkHandler.isConnected) {
            true -> {
                feedSourceNetDataSource
                    .get()
                    .getFeedSource()
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
        }.onErrorReturn {
            exceptionHandler(it)
                .asLeft()
        }
}