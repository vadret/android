package fi.kroon.vadret.settings

import fi.kroon.vadret.data.aboutinfo.AboutInfoRepository
import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import fi.kroon.vadret.data.failure.Failure
import io.reactivex.Single
import se.fimbla.either.Either
import javax.inject.Inject

class GetAboutInfoTask @Inject constructor(
    private val aboutInfoRepository: AboutInfoRepository
) {
    operator fun invoke(): Single<Either<Failure, List<AboutInfo>>> =
        aboutInfoRepository()
}