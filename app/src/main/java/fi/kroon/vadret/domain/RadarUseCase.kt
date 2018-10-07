package fi.kroon.vadret.domain

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.radar.RadarRepository
import fi.kroon.vadret.data.radar.RadarRequest
import fi.kroon.vadret.data.radar.model.Radar
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class RadarUseCase @Inject constructor(
    private val radarRepository: RadarRepository
) {
    fun get(radarRequest: RadarRequest): Single<Either<Failure, Radar>> {
        return Single.just(radarRequest)
            .flatMap { _ ->
                radarRepository.get(radarRequest)
            }.doOnEvent { t1, t2 ->
                Timber.d("T1: $t1, T2: $t2")
            }.doOnError {
                Timber.d("$it")
            }
    }
}