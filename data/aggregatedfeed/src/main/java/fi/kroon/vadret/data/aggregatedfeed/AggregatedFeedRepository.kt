package fi.kroon.vadret.data.aggregatedfeed

import dagger.Lazy
import fi.kroon.vadret.data.aggregatedfeed.mapper.AggregatedFeedMapper
import fi.kroon.vadret.data.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.data.aggregatedfeed.model.KrisinformationFeed
import fi.kroon.vadret.data.aggregatedfeed.model.SmhiWarning
import fi.kroon.vadret.data.aggregatedfeed.net.KrisinformationNetDataSource
import fi.kroon.vadret.data.aggregatedfeed.net.SmhiWarningNetDataSource
import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.exception.ExceptionHandler
import fi.kroon.vadret.data.exception.IErrorHandler
import fi.kroon.vadret.data.exception.IExceptionHandler
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.util.FEED_SOURCE_KRISINFORMATION_ID
import fi.kroon.vadret.util.FEED_SOURCE_SMHI_ID
import fi.kroon.vadret.util.NetworkHandler
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import fi.kroon.vadret.util.extension.asSingle
import io.github.sphrak.either.Either
import io.github.sphrak.either.flatMap
import io.github.sphrak.either.map
import io.reactivex.Single
import org.threeten.bp.OffsetDateTime
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class AggregatedFeedRepository @Inject constructor(
    private val networkHandler: NetworkHandler,
    private val smhiWarningNetDataSource: Lazy<SmhiWarningNetDataSource>,
    private val krisinformationNetDataSource: Lazy<KrisinformationNetDataSource>,
    private val errorHandler: ErrorHandler,
    private val exceptionHandler: ExceptionHandler
) : IErrorHandler by errorHandler, IExceptionHandler<Failure> by exceptionHandler {

    /**
     *  Merges SMHI warnings with Krisinformation news and VMAs
     *  from the enabled [feeds], newest first. When a
     *  [response.body] is null [NetworkResponseEmpty] is
     *  returned.
     */
    operator fun invoke(feeds: List<Int>, counties: List<Int>): Single<Either<Failure, List<AggregatedFeed>>> =
        when (networkHandler.isConnected) {
            true -> {
                getSmhiWarnings(
                    isEnabled = FEED_SOURCE_SMHI_ID in feeds,
                    counties = counties
                ).zipWith(
                    getKrisinformation(
                        isEnabled = FEED_SOURCE_KRISINFORMATION_ID in feeds,
                        counties = counties
                    )
                ) { smhiEither: Either<Failure, List<AggregatedFeed>>, krisinformationEither: Either<Failure, List<AggregatedFeed>> ->
                    smhiEither.flatMap { smhiList: List<AggregatedFeed> ->
                        krisinformationEither.map { krisinformationList: List<AggregatedFeed> ->
                            (smhiList + krisinformationList)
                                .sortedByDescending { OffsetDateTime.parse(it.published) }
                        }
                    }
                }
            }
            false -> getNetworkOfflineError()
        }.onErrorReturn {
            exceptionHandler(it)
                .asLeft()
        }

    private fun getSmhiWarnings(isEnabled: Boolean, counties: List<Int>): Single<Either<Failure, List<AggregatedFeed>>> =
        when (isEnabled) {
            true ->
                smhiWarningNetDataSource
                    .get()
                    .getWarnings()
                    .map { response: Response<List<SmhiWarning>> ->
                        response
                            .body()
                            ?.let { warningList: List<SmhiWarning> ->
                                AggregatedFeedMapper
                                    .fromSmhi(warningList = warningList, districtIds = counties)
                                    .asRight()
                            }
                            ?: Failure.NetworkResponseEmpty
                                .asLeft()
                    }
            false -> emptyList<AggregatedFeed>().asRight().asSingle()
        }

    private fun getKrisinformation(isEnabled: Boolean, counties: List<Int>): Single<Either<Failure, List<AggregatedFeed>>> =
        when (isEnabled) {
            true -> {
                val countyCodes: String = counties.joinToString(",")
                krisinformationNetDataSource
                    .get()
                    .getNews(counties = countyCodes)
                    .zipWith(getVmas(countyCodes = countyCodes)) { response: Response<List<KrisinformationFeed>>, vmaList: List<AggregatedFeed> ->
                        response
                            .body()
                            ?.let { newsList: List<KrisinformationFeed> ->
                                (AggregatedFeedMapper.fromKrisinformation(feedList = newsList, isVma = false) + vmaList)
                                    .asRight()
                            }
                            ?: Failure.NetworkResponseEmpty
                                .asLeft()
                    }
            }
            false -> emptyList<AggregatedFeed>().asRight().asSingle()
        }

    /**
     *  The VMA feed was empty while this was written, so its
     *  payload is unverified. A failure here should not hide
     *  news and SMHI warnings.
     */
    private fun getVmas(countyCodes: String): Single<List<AggregatedFeed>> =
        krisinformationNetDataSource
            .get()
            .getVmas(counties = countyCodes)
            .map { response: Response<List<KrisinformationFeed>> ->
                AggregatedFeedMapper.fromKrisinformation(
                    feedList = response.body().orEmpty(),
                    isVma = true
                )
            }.doOnError {
                Timber.e("Loading VMAs failed: $it")
            }.onErrorReturnItem(emptyList())
}