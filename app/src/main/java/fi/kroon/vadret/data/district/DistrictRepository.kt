package fi.kroon.vadret.data.district

import fi.kroon.vadret.data.district.model.DistrictView
import fi.kroon.vadret.data.district.net.DistrictNetDataSource
import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.exception.IErrorHandler
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.di.scope.CoreApplicationScope
import fi.kroon.vadret.util.NetworkHandler
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.reactivex.Single
import javax.inject.Inject
import retrofit2.Response

@CoreApplicationScope
class DistrictRepository @Inject constructor(
    private val networkHandler: NetworkHandler,
    private val networkDataSource: DistrictNetDataSource,
    private val errorHandler: ErrorHandler
) : IErrorHandler by errorHandler {
    /**
     *  When [response.body] is null [NetworkResponseEmpty] is
     *  returned.
     */
    operator fun invoke(): Single<Either<Failure, DistrictView>> =
        when (networkHandler.isConnected) {
            true -> {
                networkDataSource()
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
        }
}