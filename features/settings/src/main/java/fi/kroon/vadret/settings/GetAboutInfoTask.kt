package fi.kroon.vadret.settings

import fi.kroon.vadret.data.aboutinfo.AboutInfoRepository
import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import fi.kroon.vadret.data.failure.Failure
import io.github.sphrak.either.Either
import javax.inject.Inject

class GetAboutInfoTask @Inject constructor(
    private val aboutInfoRepository: AboutInfoRepository
) {
    suspend operator fun invoke(): Either<Failure, List<AboutInfo>> =
        aboutInfoRepository()
}