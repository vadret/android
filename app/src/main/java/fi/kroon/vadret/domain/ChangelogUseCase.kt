package fi.kroon.vadret.domain

import fi.kroon.vadret.data.changelog.ChangelogRepository
import fi.kroon.vadret.data.changelog.exception.ChangelogFailure
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class ChangelogUseCase @Inject constructor(
    private val changelogRepository: ChangelogRepository
) {
    fun get(): Single<Either<Failure, String>> {
        return changelogRepository.get()
            .doOnEvent { t1, t2 ->
                Timber.d("T1: $t1, T2: $t2")
            }.doOnError {
                Timber.d("$it")
            }.onErrorReturn { _ ->
                Either.Left(ChangelogFailure.FileNotAvailableFailure())
            }
    }
}