package fi.kroon.vadret.data.aboutinfo.local

import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.util.extension.asRight
import fi.kroon.vadret.util.extension.asSingle
import io.reactivex.Single
import se.fimbla.either.Either
import javax.inject.Inject

/**
 * The rows reference app resources, so the app provides them.
 */
class AboutInfoLocalDataSource @Inject constructor(
    private val aboutInfoList: List<AboutInfo>
) {

    operator fun invoke(): Single<Either<Failure, List<AboutInfo>>> =
        aboutInfoList
            .asRight()
            .asSingle()
}