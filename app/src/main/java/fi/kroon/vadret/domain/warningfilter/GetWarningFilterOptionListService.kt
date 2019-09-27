package fi.kroon.vadret.domain.warningfilter

import fi.kroon.vadret.data.district.model.DistrictOptionEntity
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.feedsource.model.FeedSourceOptionEntity
import fi.kroon.vadret.util.extension.flatMapSingle
import io.github.sphrak.either.Either
import io.github.sphrak.either.map
import io.reactivex.Single
import javax.inject.Inject
import timber.log.Timber

class GetWarningFilterOptionListService @Inject constructor(
    private val getAppFeedSourceStateTask: GetAppFeedSourceStateTask,
    private val getAppDistrictStateTask: GetAppDistrictStateTask
) {

    data class Data(
        val districtOptionList: List<DistrictOptionEntity> = emptyList(),
        val feedSourceOptionList: List<FeedSourceOptionEntity> = emptyList()
    )

    operator fun invoke(): Single<Either<Failure, Data>> =
        Single.just(Data())
            .flatMap(::getAppFeedSourceState)
            .flatMap(::getAppDistrictStateTask)

    private fun getAppFeedSourceState(data: Data): Single<Either<Failure, Data>> =
        getAppFeedSourceStateTask()
            .map { either: Either<Failure, List<FeedSourceOptionEntity>> ->
                either.map { feedSourceOptionList: List<FeedSourceOptionEntity> ->
                    feedSourceOptionList.forEach {
                        Timber.d("FeedSourceOption: $it")
                    }
                    data.copy(feedSourceOptionList = feedSourceOptionList)
                }
            }

    private fun getAppDistrictStateTask(either: Either<Failure, Data>): Single<Either<Failure, Data>> =
        either.flatMapSingle { data: Data ->
            getAppDistrictStateTask()
                .map { result: Either<Failure, List<DistrictOptionEntity>> ->
                    result.map { districtOptionList: List<DistrictOptionEntity> ->
                        districtOptionList.forEach {
                            Timber.d("DistrictOption: $it")
                        }
                        data.copy(districtOptionList = districtOptionList)
                    }
                }
        }
}