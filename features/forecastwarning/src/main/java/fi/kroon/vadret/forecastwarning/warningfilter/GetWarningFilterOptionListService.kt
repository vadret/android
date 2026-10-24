package fi.kroon.vadret.forecastwarning.warningfilter

import fi.kroon.vadret.data.district.model.DistrictOptionEntity
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.feedsource.model.FeedSourceOptionEntity
import io.github.sphrak.either.Either
import io.github.sphrak.either.flatMap
import io.github.sphrak.either.map
import timber.log.Timber
import javax.inject.Inject

class GetWarningFilterOptionListService @Inject constructor(
    private val getAppFeedSourceStateTask: GetAppFeedSourceStateTask,
    private val getAppDistrictStateTask: GetAppDistrictStateTask
) {

    data class Data(
        val districtOptionList: List<DistrictOptionEntity> = emptyList(),
        val feedSourceOptionList: List<FeedSourceOptionEntity> = emptyList()
    )

    suspend operator fun invoke(): Either<Failure, Data> =
        getAppFeedSourceState(Data())
            .let { getAppDistrictStateTask(it) }

    private suspend fun getAppFeedSourceState(data: Data): Either<Failure, Data> =
        getAppFeedSourceStateTask()
            .let { either: Either<Failure, List<FeedSourceOptionEntity>> ->
                either.map { feedSourceOptionList: List<FeedSourceOptionEntity> ->
                    feedSourceOptionList.forEach {
                        Timber.d("FeedSourceOption: $it")
                    }
                    data.copy(feedSourceOptionList = feedSourceOptionList)
                }
            }

    private suspend fun getAppDistrictStateTask(either: Either<Failure, Data>): Either<Failure, Data> =
        either.flatMap { data: Data ->
            getAppDistrictStateTask()
                .let { result: Either<Failure, List<DistrictOptionEntity>> ->
                    result.map { districtOptionList: List<DistrictOptionEntity> ->
                        districtOptionList.forEach {
                            Timber.d("DistrictOption: $it")
                        }
                        data.copy(districtOptionList = districtOptionList)
                    }
                }
        }
}