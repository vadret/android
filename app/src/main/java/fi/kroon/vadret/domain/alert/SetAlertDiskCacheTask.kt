package fi.kroon.vadret.domain.alert

import fi.kroon.vadret.data.alert.cache.AlertCacheDataSource
import fi.kroon.vadret.data.alert.model.Alert
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class SetAlertDiskCacheTask @Inject constructor(
    private val repo: AlertCacheDataSource
) {
    operator fun invoke(alert: Alert): Single<Either<Failure, Alert>> =
        repo
            .updateDiskCache(alert)
            .doOnError {
                Timber.e("$it")
            }
}