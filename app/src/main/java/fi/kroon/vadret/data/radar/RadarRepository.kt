package fi.kroon.vadret.data.radar

import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.radar.exception.RadarFailure
import fi.kroon.vadret.data.radar.model.Radar
import fi.kroon.vadret.data.radar.model.RadarRequest
import fi.kroon.vadret.data.radar.net.RadarApi
import fi.kroon.vadret.utils.NetworkHandler
import fi.kroon.vadret.utils.extensions.asLeft
import fi.kroon.vadret.utils.extensions.asRight
import fi.kroon.vadret.utils.extensions.asSingle
import io.reactivex.Single
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class RadarRepository @Inject constructor(
    private val radarApi: RadarApi,
    private val networkHandler: NetworkHandler
) {
    fun get(radarRequest: RadarRequest): Single<Either<Failure, Radar>> {
        return when (networkHandler.isConnected) {
            true -> with(radarRequest) {
                radarApi.get(year = year, month = month, date = date, format = format, timeZone = timeZone)
            }.map { response: Response<Radar> ->
                Timber.d("Response: ${response.body()}")
                when (response.body()?.files) {
                    null -> {
                        RadarFailure
                            .NoRadarAvailable
                            .asLeft()
                    }
                    else -> {
                        response.body()!!
                            .asRight()
                    }
                }
            }.doOnEvent { t1, t2 ->
                Timber.d("T1: $t1, T2: $t2")
            }.doOnError {
                Timber.d("DisplayError occured: $it")
            }.onErrorReturn {
                Either.Left(Failure.NetworkException)
            }
            false -> {
                Failure
                    .NetworkOfflineFailure
                    .asLeft()
                    .asSingle()
            }
        }
    }
}