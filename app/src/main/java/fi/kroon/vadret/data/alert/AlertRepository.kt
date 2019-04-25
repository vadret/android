package fi.kroon.vadret.data.alert

import fi.kroon.vadret.di.scope.CoreApplicationScope
import fi.kroon.vadret.data.alert.exception.AlertFailure
import fi.kroon.vadret.data.alert.model.Alert
import fi.kroon.vadret.data.alert.net.AlertNetDataSource
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.functional.Either.Left
import fi.kroon.vadret.data.functional.Either.Right
import fi.kroon.vadret.utils.HTTP_200_OK
import fi.kroon.vadret.utils.HTTP_304_NOT_MODIFIED
import fi.kroon.vadret.utils.HTTP_400_BAD_REQUEST
import fi.kroon.vadret.utils.HTTP_403_FORBIDDEN
import fi.kroon.vadret.utils.HTTP_500_INTERNAL_SERVER_ERROR
import fi.kroon.vadret.utils.HTTP_503_SERVICE_UNAVAILABLE
import fi.kroon.vadret.utils.HTTP_504_GATEWAY_TIMEOUT
import fi.kroon.vadret.utils.NetworkHandler
import fi.kroon.vadret.utils.extensions.asLeft
import fi.kroon.vadret.utils.extensions.asSingle
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

@CoreApplicationScope
class AlertRepository @Inject constructor(
    private val alertApi: AlertNetDataSource,
    private val networkHandler: NetworkHandler
) {
    operator fun invoke(): Single<Either<Failure, Alert>> =
        when (networkHandler.isConnected) {
            true -> alertApi.get().map {
                when (it.code()) {
                    HTTP_200_OK -> when (it.body()?.warnings) {
                        null -> {
                            Timber.d("Body was null? ${it.body()} - ${it.code()}")
                            Left(AlertFailure.NoAlertAvailable)
                        }
                        else -> {
                            Timber.d("Not null: ${it.body()}")
                            Right(it.body()!!)
                        }
                    }
                    HTTP_304_NOT_MODIFIED -> Left(Failure.HttpNotModified304)
                    HTTP_403_FORBIDDEN -> Left(Failure.HttpForbidden403)
                    HTTP_400_BAD_REQUEST -> Left(Failure.HttpBadRequest400)
                    HTTP_500_INTERNAL_SERVER_ERROR -> Left(Failure.HttpInternalServerError500)
                    HTTP_503_SERVICE_UNAVAILABLE -> Left(Failure.HttpServiceUnavailable503)
                    HTTP_504_GATEWAY_TIMEOUT -> Left(Failure.HttpGatewayTimeout504)
                    else -> Left(AlertFailure.NoAlertAvailable)
                }
            }.doOnError {
                Timber.e("Error occured: $it")
            }.onErrorReturn {
                Failure
                    .NetworkException
                    .asLeft()
            }
            false ->
                Failure
                    .NetworkOfflineFailure
                    .asLeft()
                    .asSingle()
        }
}