package fi.kroon.vadret.domain.district

import fi.kroon.vadret.data.district.DistrictRepository
import fi.kroon.vadret.data.district.model.DistrictView
import fi.kroon.vadret.data.exception.Failure
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject

class GetDistrictNetTask @Inject constructor(
    private val repo: DistrictRepository
) {
    operator fun invoke(): Single<Either<Failure, DistrictView>> =
        repo()
}