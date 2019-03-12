package fi.kroon.vadret.domain.alert

import fi.kroon.vadret.data.alert.model.Alert
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.functional.flatMap
import fi.kroon.vadret.data.functional.flatMapSingle
import fi.kroon.vadret.data.functional.map
import fi.kroon.vadret.domain.BaseService
import fi.kroon.vadret.presentation.alert.AlertMapper
import fi.kroon.vadret.presentation.alert.model.BaseWarningItemModel
import fi.kroon.vadret.utils.FIVE_MINUTES_IN_MILLIS
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith
import timber.log.Timber
import javax.inject.Inject

class GetAlertService @Inject constructor(
    private val getAlertTask: GetAlertTask,
    private val getAlertDiskCacheTask: GetAlertDiskCacheTask,
    private val getAlertMemoryCacheTask: GetAlertMemoryCacheTask,
    private val setAlertDiskCacheTask: SetAlertDiskCacheTask,
    private val setAlertMemoryCacheTask: SetAlertMemoryCacheTask
) : BaseService() {

    data class Data(
        val baseWarningItemModelList: List<BaseWarningItemModel> = listOf(),
        val alert: Alert? = null,
        val forceNet: Boolean = false,
        val timeStamp: Long? = null
    )

    operator fun invoke(timeStamp: Long?, forceNet: Boolean): Single<Either<Failure, Data>> =
        Single.just(Data(timeStamp = timeStamp, forceNet = forceNet))
            .flatMap(::getAlertList)
            .map(::mapAlertToAlertModelList)

    private fun getAlertList(data: Data): Single<Either<Failure, Data>> =
        getAlert(data)
            .map { either: Either<Failure, Data> ->
                either.map { dataIn: Data ->
                    dataIn.copy(
                        timeStamp = currentTimeMillis
                    )
                }
            }

    private fun getAlert(data: Data): Single<Either<Failure, Data>> =
        with(data) {
            when {
                forceNet || (currentTimeMillis > (timeStamp!! + FIVE_MINUTES_IN_MILLIS)) -> {
                    getAlertTask()
                        .map { either: Either<Failure, Alert> ->
                            either.map { alert: Alert ->
                                data.copy(alert = alert)
                            }
                        }.flatMap { either: Either<Failure, Data> ->
                            updateCache(either)
                        }
                }
                else -> {
                    Single.merge(
                        getAlertMemoryCacheTask()
                            .map { either: Either<Failure, Alert> ->
                                either.map { alert: Alert ->
                                    data.copy(alert = alert)
                                }
                            },
                        getAlertDiskCacheTask()
                            .map { either ->
                                either.map { alert: Alert ->
                                    data.copy(alert = alert)
                                }
                            }
                    ).filter { result: Either<Failure, Data> ->
                        result.either(
                            {
                                false
                            },
                            { data: Data ->
                                Timber.d("Fetched from cache ${data.alert}")
                                data
                                    .alert!!
                                    .warnings!!
                                    .isNotEmpty()
                            }
                        )
                    }.take(1)
                        .switchIfEmpty(
                            getAlertTask()
                                .map { either: Either<Failure, Alert> ->
                                    Timber.d("Cache was empty, fetching from network.")
                                    either.map { alert: Alert ->
                                        data.copy(alert = alert)
                                    }
                                }.flatMap { data: Either<Failure, Data> ->
                                    updateCache(data)
                                }.toFlowable()
                        ).singleOrError()
                }
            }
        }

    private fun updateCache(either: Either<Failure, Data>): Single<Either<Failure, Data>> =
        either.flatMapSingle { data: Data ->
            setAlertMemoryCacheTask(data.alert!!)
                .zipWith(setAlertDiskCacheTask(data.alert))
                .map { pair: Pair<Either<Failure, Alert>,
                    Either<Failure, Alert>> ->
                    Timber.i("Updating cache")
                    val (firstEither: Either<Failure, Alert>, secondEither: Either<Failure, Alert>) = pair
                    firstEither.flatMap { _: Alert ->
                        secondEither.map { _: Alert ->
                            data
                        }
                    }
                }
        }

    private fun mapAlertToAlertModelList(either: Either<Failure, Data>): Either<Failure, Data> =
        either.map { data: Data ->
            val baseWarningItemModelList: List<BaseWarningItemModel> = AlertMapper.toWarningModelList(data.alert!!.warnings!!)
            val sortedBaseWarningItemModelList: List<BaseWarningItemModel> =
                baseWarningItemModelList
                    .sortedWith(
                        compareBy { baseWarningItemModel: BaseWarningItemModel ->
                            baseWarningItemModel.timeStamp
                        }
                    ).reversed()
            data.copy(baseWarningItemModelList = sortedBaseWarningItemModelList)
        }
}