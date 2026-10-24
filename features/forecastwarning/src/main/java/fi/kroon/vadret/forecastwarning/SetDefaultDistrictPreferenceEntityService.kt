package fi.kroon.vadret.forecastwarning

import fi.kroon.vadret.data.district.model.DistrictEntity
import fi.kroon.vadret.data.districtpreference.model.DistrictPreferenceEntity
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.forecastwarning.district.GetAllDistrictTask
import fi.kroon.vadret.forecastwarning.districtpreference.SetDistrictPreferenceListTask
import fi.kroon.vadret.util.APP_WARNING_FILTER_KEY
import io.github.sphrak.either.Either
import io.github.sphrak.either.flatMap
import io.github.sphrak.either.map
import timber.log.Timber
import javax.inject.Inject

class SetDefaultDistrictPreferenceEntityService @Inject constructor(
    private val getAllDistrictTask: GetAllDistrictTask,
    private val setDistrictPreferenceListTask: SetDistrictPreferenceListTask
) {

    data class Data(
        val districtPreferenceList: MutableList<DistrictPreferenceEntity> = mutableListOf()
    )

    private companion object {
        // Stockholms län
        const val DEFAULT_DISTRICT_ID = 1
    }

    suspend operator fun invoke(): Either<Failure, Boolean> =
        buildDistrictPreferenceList(Data())
            .let { setDistrictPreferenceList(it) }

    private suspend fun buildDistrictPreferenceList(data: Data): Either<Failure, Data> =
        getAllDistrictTask()
            .let { result: Either<Failure, List<DistrictEntity>> ->
                result.map { ids: List<DistrictEntity> ->
                    ids.forEach { district: DistrictEntity ->

                        val isEnabled: Boolean = district.id == DEFAULT_DISTRICT_ID
                        val districtPreferenceEntity = DistrictPreferenceEntity(
                            districtId = district.id,
                            usedBy = APP_WARNING_FILTER_KEY,
                            isEnabled = isEnabled
                        )
                        data.districtPreferenceList.add(districtPreferenceEntity)
                    }
                    data
                }
            }

    private suspend fun setDistrictPreferenceList(either: Either<Failure, Data>): Either<Failure, Boolean> = either
        .flatMap { data: Data ->
            setDistrictPreferenceListTask(data.districtPreferenceList)
                .let { result: Either<Failure, List<Long>> ->
                    result.map { ids: List<Long> ->
                        Timber.d("INSERTED DISTRICT: $ids")
                        true
                    }
                }
        }
}