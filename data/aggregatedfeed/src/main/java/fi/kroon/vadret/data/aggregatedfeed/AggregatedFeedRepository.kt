package fi.kroon.vadret.data.aggregatedfeed

import dagger.Lazy
import fi.kroon.vadret.data.aggregatedfeed.mapper.AggregatedFeedMapper
import fi.kroon.vadret.data.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.data.aggregatedfeed.model.KrisinformationFeed
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
import fi.kroon.vadret.util.extension.catchFailure
import io.github.sphrak.either.Either
import io.github.sphrak.either.flatMap
import io.github.sphrak.either.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Instant

class AggregatedFeedRepository @Inject constructor(
    private val networkHandler: NetworkHandler,
    private val smhiWarningNetDataSource: Lazy<SmhiWarningNetDataSource>,
    private val krisinformationNetDataSource: Lazy<KrisinformationNetDataSource>,
    private val errorHandler: ErrorHandler,
    private val exceptionHandler: ExceptionHandler
) : IErrorHandler by errorHandler, IExceptionHandler<Failure> by exceptionHandler {

    /**
     *  Merges SMHI warnings with Krisinformation news and VMAs
     *  from the enabled [feeds], newest first. A VMA can also
     *  be listed as news, so items are unique by identifier. When a
     *  [response.body] is null [NetworkResponseEmpty] is
     *  returned.
     */
    suspend operator fun invoke(feeds: List<Int>, counties: List<Int>): Either<Failure, List<AggregatedFeed>> =
        withContext(Dispatchers.IO) {
            when (networkHandler.isConnected) {
                true -> coroutineScope {
                    val smhi = async {
                        getSmhiWarnings(
                            isEnabled = FEED_SOURCE_SMHI_ID in feeds,
                            counties = counties
                        )
                    }
                    val krisinformation = async {
                        getKrisinformation(
                            isEnabled = FEED_SOURCE_KRISINFORMATION_ID in feeds,
                            counties = counties
                        )
                    }
                    val krisinformationEither: Either<Failure, List<AggregatedFeed>> = krisinformation.await()
                    smhi.await().flatMap { smhiList: List<AggregatedFeed> ->
                        krisinformationEither.map { krisinformationList: List<AggregatedFeed> ->
                            (smhiList + krisinformationList)
                                .distinctBy { aggregatedFeed: AggregatedFeed -> aggregatedFeed.identifier }
                                .sortedByDescending { aggregatedFeed: AggregatedFeed -> Instant.parse(aggregatedFeed.published) }
                        }
                    }
                }
                false -> getNetworkOfflineError()
            }
        }

    private suspend fun getSmhiWarnings(isEnabled: Boolean, counties: List<Int>): Either<Failure, List<AggregatedFeed>> =
        when (isEnabled) {
            true -> catchFailure {
                smhiWarningNetDataSource
                    .get()
                    .getWarnings()
                    ?.let { warningList ->
                        AggregatedFeedMapper
                            .fromSmhi(warningList = warningList, districtIds = counties)
                            .asRight()
                    }
                    ?: Failure.NetworkResponseEmpty
                        .asLeft()
            }
            false -> emptyList<AggregatedFeed>().asRight()
        }

    private suspend fun getKrisinformation(isEnabled: Boolean, counties: List<Int>): Either<Failure, List<AggregatedFeed>> =
        when (isEnabled) {
            true -> catchFailure {
                val countyCodes: String = counties.joinToString(",")
                val vmaList: List<AggregatedFeed> = getVmas(countyCodes = countyCodes)
                krisinformationNetDataSource
                    .get()
                    .getNews(counties = countyCodes)
                    ?.let { newsList: List<KrisinformationFeed> ->
                        (AggregatedFeedMapper.fromKrisinformation(feedList = newsList, isVma = false) + vmaList)
                            .asRight()
                    }
                    ?: Failure.NetworkResponseEmpty
                        .asLeft()
            }
            false -> emptyList<AggregatedFeed>().asRight()
        }

    /**
     *  The VMA feed was empty while this was written, so its
     *  payload is unverified. A failure here should not hide
     *  news and SMHI warnings.
     */
    private suspend fun getVmas(countyCodes: String): List<AggregatedFeed> =
        try {
            AggregatedFeedMapper.fromKrisinformation(
                feedList = krisinformationNetDataSource
                    .get()
                    .getVmas(counties = countyCodes)
                    .orEmpty(),
                isVma = true
            )
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Timber.e("Loading VMAs failed: $exception")
            emptyList()
        }
}