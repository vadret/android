package fi.kroon.vadret.domain.districtpreference

import fi.kroon.vadret.data.districtpreference.DistrictPreferenceRepository
import fi.kroon.vadret.data.failure.Failure
import io.github.sphrak.either.Either
import io.reactivex.Single
import javax.inject.Inject

class DeleteAllDistrictPreferenceTask @Inject constructor(
    private val repo: DistrictPreferenceRepository
) {
    operator fun invoke(): Single<Either<Failure, Unit>> = repo
        .deleteAll()
}