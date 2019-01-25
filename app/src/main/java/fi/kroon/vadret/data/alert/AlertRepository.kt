package fi.kroon.vadret.data.alert

import fi.kroon.vadret.data.alert.exception.AlertFailure
import fi.kroon.vadret.data.alert.model.Alert
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.functional.Either.Right
import fi.kroon.vadret.data.functional.Either.Left
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.di.scope.VadretApplicationScope
import fi.kroon.vadret.utils.NetworkHandler
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

@VadretApplicationScope
class AlertRepository @Inject constructor(
    private val alertApi: AlertApi,
    private val networkHandler: NetworkHandler
) {
    fun get(): Single<Either<Failure, Alert>> {
        return when (networkHandler.isConnected) {
            true -> alertApi.get().map {
                when (it.code()) {
                    200 -> when (it.body()?.alert) {
                        null -> Left(AlertFailure.NoAlertAvailable())
                        else -> Right(it.body()!!)
                    }
                    304 -> Left(Failure.HttpNotModified304())
                    403 -> Left(Failure.HttpForbidden403())
                    400 -> Left(Failure.HttpBadRequest400())
                    500 -> Left(Failure.HttpInternalServerError500())
                    503 -> Left(Failure.HttpServiceUnavailable503())
                    504 -> Left(Failure.HttpGatewayTimeout504())
                    else -> Left(AlertFailure.NoAlertAvailable())
                }
            }.doOnError {
                Timber.d("DisplayError occured: $it")
            }.onErrorReturn {
                Either.Left(Failure.NetworkException())
            }
            false -> Single.just(Either.Left(Failure.NetworkOfflineFailure()))
        }
    }
}