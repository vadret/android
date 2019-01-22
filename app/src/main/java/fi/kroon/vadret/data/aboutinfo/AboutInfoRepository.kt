package fi.kroon.vadret.data.aboutinfo

import fi.kroon.vadret.data.aboutinfo.local.AboutInfoEntity
import fi.kroon.vadret.data.aboutinfo.local.AboutInfoLocalDataSource
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import io.reactivex.Single
import javax.inject.Inject

class AboutInfoRepository @Inject constructor(
    private val local: AboutInfoLocalDataSource
) {
    fun get(): Single<Either<Failure, List<AboutInfoEntity>>> =
        local.get()
}