package fi.kroon.vadret.domain.aboutapp

import fi.kroon.vadret.data.aboutinfo.AboutInfoRepository
import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import fi.kroon.vadret.data.failure.Failure
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject

class GetAboutInfoTask @Inject constructor(
    private val aboutInfoRepository: AboutInfoRepository
) {
    operator fun invoke(): Single<Either<Failure, List<AboutInfo>>> =
        aboutInfoRepository()
}