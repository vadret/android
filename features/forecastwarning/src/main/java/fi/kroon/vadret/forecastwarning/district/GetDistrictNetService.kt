package fi.kroon.vadret.forecastwarning.district

import fi.kroon.vadret.data.district.model.District
import fi.kroon.vadret.data.district.model.DistrictEntity
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.forecastwarning.CountDistrictEntityTask
import io.github.sphrak.either.Either
import io.github.sphrak.either.flatMap
import io.github.sphrak.either.map
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

    suspend operator fun invoke(): Either<Failure, Boolean> =
        getDistrictAvailabilityStatus(Data())
            .let { checkDistrictAvailability(it) }
            .let { setDistrictEntityList(it) }
            .let { transform(it) }

    private suspend fun getDistrictAvailabilityStatus(data: Data): Either<Failure, Data> =
        countDistrictEntityTask()
            .let { either: Either<Failure, Boolean> ->
                either.map { districtIsAvailable: Boolean ->
                    data.copy(
                        districtIsAvailable = districtIsAvailable
                    )
                }
            }

    private suspend fun checkDistrictAvailability(either: Either<Failure, Data>): Either<Failure, Data> =
        either.flatMap { data: Data ->
            when (data.districtIsAvailable) {
                false -> getDistrictNet(either)
                true -> either
            }
        }

    private suspend fun getDistrictNet(either: Either<Failure, Data>): Either<Failure, Data> =
        either.flatMap { data: Data ->
            getDistrictNetTask()
                .let { result: Either<Failure, List<District>> ->
                    result.map { districtList: List<District> ->
                        Timber.d("GET DISTRICT LIST NET")
                        districtList.forEach {
                            Timber.d("$it")
                        }
                        data.copy(districtList = districtList)
                    }
                }
        }

    private suspend fun setDistrictEntityList(either: Either<Failure, Data>): Either<Failure, Data> =
        either.flatMap { data: Data ->
            setDistrictListTask(data.districtList)
                .let { result: Either<Failure, List<Long>> ->
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