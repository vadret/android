package fi.kroon.vadret.forecastwarning.district

import fi.kroon.vadret.data.district.DistrictRepository
import fi.kroon.vadret.data.district.model.District
import fi.kroon.vadret.data.failure.Failure
import io.github.sphrak.either.Either
import javax.inject.Inject

class GetDistrictNetTask @Inject constructor(
    private val repo: DistrictRepository
) {
    suspend operator fun invoke(): Either<Failure, List<District>> =
        repo()
}