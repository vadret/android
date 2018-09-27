package fi.kroon.vadret.data.changelog

import fi.kroon.vadret.R
import fi.kroon.vadret.data.changelog.exception.ChangelogFailure
import fi.kroon.vadret.data.common.RawTextFileReader
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class ChangelogRepository
@Inject constructor(private val fileReader: RawTextFileReader) {
    fun get(): Single<Either<Failure, String>> {
        return Single.just(
            fileReader.readFile(R.raw.changelog)
        ).doOnEvent { t1, t2 ->
            Timber.d("T1: $t1, T2: $t2")
        }.doOnError {
            Timber.e("$it")
        }.onErrorReturn {
            Either.Left(ChangelogFailure.FileNotAvailableFailure())
        }
    }
}