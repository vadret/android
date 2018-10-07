package fi.kroon.vadret.presentation.viewmodel

import fi.kroon.vadret.data.alert.model.Alert
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.domain.AlertUseCase
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class AlertViewModel @Inject constructor(
    private val alertUseCase: AlertUseCase
) : BaseViewModel() {
    fun get(): Single<Either<Failure, Alert>> = alertUseCase.get()
        .doOnEvent { t1, t2 ->
            Timber.d("T1: $t1, T2: $t2")
        }.doOnError {
            Timber.d("$it")
        }.onErrorReturn {
            Either.Left(Failure.IOException())
        }
}