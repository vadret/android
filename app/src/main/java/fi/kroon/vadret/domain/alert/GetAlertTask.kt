package fi.kroon.vadret.domain.alert

import fi.kroon.vadret.data.alert.AlertRepository
import fi.kroon.vadret.data.alert.model.Alert
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class GetAlertTask @Inject constructor(
    private val repo: AlertRepository

) {
    operator fun invoke(): Single<Either<Failure, Alert>> =
        repo()
            .doOnError {
                Timber.e("TASK: $it")
            }
}