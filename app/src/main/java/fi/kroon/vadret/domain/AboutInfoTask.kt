package fi.kroon.vadret.domain

import fi.kroon.vadret.data.aboutinfo.AboutInfoRepository
import fi.kroon.vadret.data.aboutinfo.local.AboutInfoEntity
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import io.reactivex.Single
import javax.inject.Inject

class AboutInfoTask @Inject constructor(
    private val aboutInfoRepository: AboutInfoRepository
) {
    operator fun invoke(): Single<Either<Failure, List<AboutInfoEntity>>> =
        aboutInfoRepository
            .get()
}