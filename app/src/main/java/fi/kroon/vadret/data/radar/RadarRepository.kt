package fi.kroon.vadret.data.radar

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.radar.model.Radar
import fi.kroon.vadret.data.radar.net.RadarApi
import fi.kroon.vadret.utils.NetworkHandler
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class RadarRepository @Inject constructor(
    private val radarApi: RadarApi,
    private val networkHandler: NetworkHandler
) {
    fun get(radarRequest: RadarRequest): Single<Either<Failure, Radar>> {
        return when (networkHandler.isConnected) {
            true -> Single.just(radarRequest).flatMap { it ->
                    radarApi.get(year = it.year, month = it.month, date = it.date, format = it.format, timeZone = it.timeZone).map {
                        Timber.d("Response: ${it.body()}")
                        Either.Right(it.body()!!) as Either<Failure, Radar>
                    }
            }.doOnEvent { t1, t2 ->
                Timber.d("T1: $t1, T2: $t2")
            }.doOnError {
                Timber.d("Error occured: $it")
            }.onErrorReturn {
                Either.Left(Failure.NetworkException())
            }
            false -> Single.just(Either.Left(Failure.NetworkOfflineFailure()))
        }
    }
}