package fi.kroon.vadret.forecastwarning.districtpreference

import fi.kroon.vadret.data.districtpreference.DistrictPreferenceRepository
import fi.kroon.vadret.data.districtpreference.model.DistrictPreferenceEntity
import fi.kroon.vadret.data.failure.Failure
import io.github.sphrak.either.Either
import javax.inject.Inject

class SetDistrictPreferenceListTask @Inject constructor(
    private val repo: DistrictPreferenceRepository
) {
    suspend operator fun invoke(entityList: List<DistrictPreferenceEntity>): Either<Failure, List<Long>> =
        repo.put(entityList = entityList)
}