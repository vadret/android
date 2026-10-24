package fi.kroon.vadret.forecastwarning.districtpreference

import fi.kroon.vadret.data.districtpreference.DistrictPreferenceRepository
import fi.kroon.vadret.data.districtpreference.model.DistrictPreferenceEntity
import fi.kroon.vadret.data.failure.Failure
import io.github.sphrak.either.Either
import javax.inject.Inject

class GetDistrictPreferenceListTask @Inject constructor(
    private val repo: DistrictPreferenceRepository
) {
    suspend operator fun invoke(): Either<Failure, List<DistrictPreferenceEntity>> =
        repo.getAll()
}