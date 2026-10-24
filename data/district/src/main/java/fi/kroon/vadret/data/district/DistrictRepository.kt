package fi.kroon.vadret.data.district

import dagger.Lazy
import fi.kroon.vadret.data.district.model.District
import fi.kroon.vadret.data.district.net.DistrictNetDataSource
import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.exception.ExceptionHandler
import fi.kroon.vadret.data.exception.IErrorHandler
import fi.kroon.vadret.data.exception.IExceptionHandler
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.util.NetworkHandler
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import fi.kroon.vadret.util.extension.catchFailure
import io.github.sphrak.either.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

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
    suspend operator fun invoke(): Either<Failure, List<District>> =
        withContext(Dispatchers.IO) {
            when (networkHandler.isConnected) {
                true -> catchFailure {
                    networkDataSource.get()()
                        .body()
                        ?.asRight()
                        ?: Failure
                            .NetworkResponseEmpty
                            .asLeft()
                }
                false -> getNetworkOfflineError()
            }
        }
}