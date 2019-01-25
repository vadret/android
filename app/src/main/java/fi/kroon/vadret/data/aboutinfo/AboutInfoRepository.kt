package fi.kroon.vadret.data.aboutinfo

import fi.kroon.vadret.data.aboutinfo.exception.AboutInfoEntityFailure
import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import fi.kroon.vadret.data.aboutinfo.local.AboutInfoLocalDataSource
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.utils.extensions.asLeft
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class AboutInfoRepository @Inject constructor(
    private val aboutInfoLocalDataSource: AboutInfoLocalDataSource
) {
    operator fun invoke(): Single<Either<Failure, List<AboutInfo>>> =
        aboutInfoLocalDataSource()
            .doOnError {
                Timber.e("$it")
            }.onErrorReturn {
                AboutInfoEntityFailure.NoAboutInfoEntityAvailable().asLeft()
            }
}