package fi.kroon.vadret.domain

import fi.kroon.vadret.data.aboutinfo.AboutInfoRepository
import fi.kroon.vadret.data.aboutinfo.exception.AboutInfoEntityFailure
import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.utils.extensions.asLeft
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class AboutInfoTask @Inject constructor(
    private val aboutInfoRepository: AboutInfoRepository
) {
    operator fun invoke(): Single<Either<Failure, List<AboutInfo>>> =
        aboutInfoRepository()
            .doOnError {
                Timber.e("$it")
            }.onErrorReturn {
                AboutInfoEntityFailure.NoAboutInfoEntityAvailable().asLeft()
            }
}