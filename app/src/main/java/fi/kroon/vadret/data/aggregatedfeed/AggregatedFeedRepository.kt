package fi.kroon.vadret.data.aggregatedfeed

import fi.kroon.vadret.data.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.data.aggregatedfeed.net.AggregatedFeedNetDataSource
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.exception.IErrorHandler
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.di.scope.CoreApplicationScope
import fi.kroon.vadret.util.NetworkHandler
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.reactivex.Single
import retrofit2.Response
import javax.inject.Inject

@CoreApplicationScope
class AggregatedFeedRepository @Inject constructor(
    private val networkHandler: NetworkHandler,
    private val netDataSource: AggregatedFeedNetDataSource,
    private val errorHandler: ErrorHandler
) : IErrorHandler by errorHandler {

    /**
     *  When [response.body] is null [NetworkResponseEmpty] is
     *  returned.
     */
    operator fun invoke(feeds: String, counties: String): Single<Either<Failure, List<AggregatedFeed>>> =
        when (networkHandler.isConnected) {
            true -> {
                netDataSource(
                    feeds = feeds,
                    counties = counties
                ).map { response: Response<List<AggregatedFeed>> ->
                    response
                        .body()
                        ?.asRight()
                        ?: Failure.NetworkResponseEmpty
                            .asLeft()
                }
            }
            false -> getNetworkOfflineError()
        }
}