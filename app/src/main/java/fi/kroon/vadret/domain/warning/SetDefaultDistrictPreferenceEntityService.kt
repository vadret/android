package fi.kroon.vadret.domain.warning

import fi.kroon.vadret.data.district.model.DistrictEntity
import fi.kroon.vadret.data.districtpreference.model.DistrictPreferenceEntity
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.domain.district.GetAllDistrictTask
import fi.kroon.vadret.domain.districtpreference.SetDistrictPreferenceListTask
import fi.kroon.vadret.util.APP_WARNING_FILTER_KEY
import fi.kroon.vadret.util.extension.flatMapSingle
import io.github.sphrak.either.Either
import io.github.sphrak.either.map
import io.reactivex.Single
import javax.inject.Inject
import timber.log.Timber

class SetDefaultDistrictPreferenceEntityService @Inject constructor(
    private val getAllDistrictTask: GetAllDistrictTask,
    private val setDistrictPreferenceListTask: SetDistrictPreferenceListTask
) {

    data class Data(
        val districtPreferenceList: MutableList<DistrictPreferenceEntity> = mutableListOf()
    )

    private companion object {
        const val DEFAULT_DISTRICT_ID = 25
    }

    operator fun invoke(): Single<Either<Failure, Boolean>> =
        Single.just(Data())
            .flatMap(::buildDistrictPreferenceList)
            .flatMap(::setDistrictPreferenceList)

    private fun buildDistrictPreferenceList(data: Data): Single<Either<Failure, Data>> =
        getAllDistrictTask()
            .map { result: Either<Failure, List<DistrictEntity>> ->
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

    private fun setDistrictPreferenceList(either: Either<Failure, Data>): Single<Either<Failure, Boolean>> = either
        .flatMapSingle { data: Data ->
            setDistrictPreferenceListTask(data.districtPreferenceList)
                .map { result: Either<Failure, List<Long>> ->
                    result.map { ids: List<Long> ->
                        Timber.d("INSERTED DISTRICT: $ids")
                        true
                    }
                }
        }
}