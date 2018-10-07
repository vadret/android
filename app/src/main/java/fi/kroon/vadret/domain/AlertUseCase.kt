package fi.kroon.vadret.domain

import fi.kroon.vadret.data.alert.AlertRepository
import fi.kroon.vadret.data.alert.model.Alert
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class AlertUseCase @Inject constructor(
    private val alertRepository: AlertRepository
) {
    fun get(): Single<Either<Failure, Alert>> {
        return alertRepository.get()
            .doOnEvent {
                t1, t2 -> Timber.d("T1: $t1, T2: $t2")
            }.doOnError {
                Timber.d("$it")
            }
    }
}