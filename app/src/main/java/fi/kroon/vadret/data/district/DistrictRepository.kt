package fi.kroon.vadret.data.district

import dagger.Lazy
import fi.kroon.vadret.data.district.model.DistrictView
import fi.kroon.vadret.data.district.net.DistrictNetDataSource
import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.exception.ExceptionHandler
import fi.kroon.vadret.data.exception.IErrorHandler
import fi.kroon.vadret.data.exception.IExceptionHandler
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.di.scope.CoreApplicationScope
import fi.kroon.vadret.util.NetworkHandler
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject
import retrofit2.Response

@CoreApplicationScope
class DistrictRepository @Inject constructor(
    private val networkHandler: NetworkHandler,
    private val networkDataSource: Lazy<DistrictNetDataSource>,
    private val errorHandler: ErrorHandler,
    private val exceptionHandler: ExceptionHandler
) : IErrorHandler by errorHandler, IExceptionHandler<Failure> by exceptionHandler {
    /**
     *  When [response.body] is null [NetworkResponseEmpty] is
     *  returned.
     */
    operator fun invoke(): Single<Either<Failure, DistrictView>> =
        when (networkHandler.isConnected) {
            true -> {
                networkDataSource.get()()
                    .map { response: Response<DistrictView> ->
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