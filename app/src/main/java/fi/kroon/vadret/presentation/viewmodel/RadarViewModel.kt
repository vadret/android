package fi.kroon.vadret.presentation.viewmodel

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.radar.RadarRequest
import fi.kroon.vadret.data.radar.model.Radar
import fi.kroon.vadret.di.scope.VadretApplicationScope
import fi.kroon.vadret.domain.RadarUseCase
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

@VadretApplicationScope
class RadarViewModel @Inject constructor(
    private val radarUseCase: RadarUseCase
) : BaseViewModel() {
    fun get(request: RadarRequest): Single<Either<Failure, Radar>> = radarUseCase
        .get(request)
        .doOnEvent { t1, t2 ->
            Timber.d("T1: $t1, T2: $t2")
        }.doOnError {
            Timber.d("$it")
        }.onErrorReturn {
            Either.Left(Failure.IOException())
        }
}