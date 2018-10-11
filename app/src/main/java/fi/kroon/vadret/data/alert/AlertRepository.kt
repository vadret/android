package fi.kroon.vadret.data.alert

import fi.kroon.vadret.data.alert.exception.AlertFailure
import fi.kroon.vadret.data.alert.model.Alert
import fi.kroon.vadret.data.exception.Either
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
                Timber.d("Response: ${it.body()}")
                when (it.body()?.alert) {
                    null -> Either.Left(AlertFailure.NoAlertAvailable())
                    else -> Either.Right(it.body()!!)
                } as Either<Failure, Alert>
            }.doOnEvent { t1, t2 ->
                Timber.d("T1: $t1, T2: $t2")
            }.doOnError {
                Timber.d("Error occured: $it")
            }.onErrorReturn {
                Either.Left(Failure.NetworkException())
            }
            false -> Single.just(Either.Left(Failure.NetworkOfflineFailure()))
        }
    }
}