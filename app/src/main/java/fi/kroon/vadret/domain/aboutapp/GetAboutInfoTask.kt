package fi.kroon.vadret.domain.aboutapp

import fi.kroon.vadret.data.aboutinfo.AboutInfoRepository
import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class GetAboutInfoTask @Inject constructor(
    private val aboutInfoRepository: AboutInfoRepository
) {
    operator fun invoke(): Single<Either<Failure, List<AboutInfo>>> =
        aboutInfoRepository()
            .doOnError {
                Timber.e("$it")
            }
}