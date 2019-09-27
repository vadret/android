package fi.kroon.vadret.data.aboutinfo

import fi.kroon.vadret.data.aboutinfo.local.AboutInfoLocalDataSource
import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import fi.kroon.vadret.data.failure.Failure
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject

class AboutInfoRepository @Inject constructor(
    private val aboutInfoLocalDataSource: AboutInfoLocalDataSource
) {
    operator fun invoke(): Single<Either<Failure, List<AboutInfo>>> =
        aboutInfoLocalDataSource()
}