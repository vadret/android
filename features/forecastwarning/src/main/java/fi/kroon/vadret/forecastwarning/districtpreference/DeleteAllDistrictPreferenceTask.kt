package fi.kroon.vadret.forecastwarning.districtpreference

import fi.kroon.vadret.data.districtpreference.DistrictPreferenceRepository
import fi.kroon.vadret.data.failure.Failure
import io.github.sphrak.either.Either
import javax.inject.Inject

class DeleteAllDistrictPreferenceTask @Inject constructor(
    private val repo: DistrictPreferenceRepository
) {
    suspend operator fun invoke(): Either<Failure, Unit> = repo
        .deleteAll()
}