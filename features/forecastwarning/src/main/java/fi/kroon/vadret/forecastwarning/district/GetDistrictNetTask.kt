package fi.kroon.vadret.forecastwarning.district

import fi.kroon.vadret.data.district.DistrictRepository
import fi.kroon.vadret.data.district.model.District
import fi.kroon.vadret.data.failure.Failure
import io.reactivex.Single
import se.fimbla.either.Either
import javax.inject.Inject

class GetDistrictNetTask @Inject constructor(
    private val repo: DistrictRepository
) {
    operator fun invoke(): Single<Either<Failure, List<District>>> =
        repo()
}