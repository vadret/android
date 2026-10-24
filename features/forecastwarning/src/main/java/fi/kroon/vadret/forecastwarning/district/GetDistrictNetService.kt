package fi.kroon.vadret.forecastwarning.district

import fi.kroon.vadret.data.district.model.District
import fi.kroon.vadret.data.district.model.DistrictEntity
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.forecastwarning.CountDistrictEntityTask
import fi.kroon.vadret.util.extension.asSingle
import fi.kroon.vadret.util.extension.flatMapSingle
import io.github.sphrak.either.Either
import io.github.sphrak.either.map
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class GetDistrictNetService @Inject constructor(
    private val getDistrictNetTask: GetDistrictNetTask,
    private val setDistrictListTask: SetDistrictListTask,
    private val countDistrictEntityTask: CountDistrictEntityTask
) {

    data class Data(
        val districtList: List<District> = emptyList(),
        val districtEntityList: List<DistrictEntity> = emptyList(),
        val districtIsAvailable: Boolean = false
    )

    operator fun invoke(): Single<Either<Failure, Boolean>> =
        Single.just(Data())
            .flatMap(::getDistrictAvailabilityStatus)
            .flatMap(::checkDistrictAvailability)
            .flatMap(::setDistrictEntityList)
            .map(::transform)

    private fun getDistrictAvailabilityStatus(data: Data): Single<Either<Failure, Data>> =
        countDistrictEntityTask()
            .map { either: Either<Failure, Boolean> ->
                either.map { districtIsAvailable: Boolean ->
                    data.copy(
                        districtIsAvailable = districtIsAvailable
                    )
                }
            }

    private fun checkDistrictAvailability(either: Either<Failure, Data>): Single<Either<Failure, Data>> =
        either.flatMapSingle { data: Data ->
            when (data.districtIsAvailable) {
                false -> getDistrictNet(either)
                true -> either.asSingle()
            }
        }

    private fun getDistrictNet(either: Either<Failure, Data>): Single<Either<Failure, Data>> =
        either.flatMapSingle { data: Data ->
            getDistrictNetTask()
                .map { result: Either<Failure, List<District>> ->
                    result.map { districtList: List<District> ->
                        Timber.d("GET DISTRICT LIST NET")
                        districtList.forEach {
                            Timber.d("$it")
                        }
                        data.copy(districtList = districtList)
                    }
                }
        }

    private fun setDistrictEntityList(either: Either<Failure, Data>): Single<Either<Failure, Data>> =
        either.flatMapSingle { data: Data ->
            setDistrictListTask(data.districtList)
                .map { result: Either<Failure, List<Long>> ->
                    result.map { ids: List<Long> ->
                        Timber.d("IDS: $ids")
                        data.copy(districtIsAvailable = true)
                    }
                }
        }

    private fun transform(either: Either<Failure, Data>): Either<Failure, Boolean> =
        either.map { data: Data ->
            data.districtIsAvailable
        }
}