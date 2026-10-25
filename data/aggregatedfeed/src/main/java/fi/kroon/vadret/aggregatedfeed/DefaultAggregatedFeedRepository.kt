package fi.kroon.vadret.aggregatedfeed

import fi.kroon.vadret.aggregatedfeed.extension.toAggregatedFeed
import fi.kroon.vadret.aggregatedfeed.extension.toAggregatedFeedEntities
import fi.kroon.vadret.aggregatedfeed.extension.toAggregatedFeedEntity
import fi.kroon.vadret.aggregatedfeed.extension.toFailure
import fi.kroon.vadret.aggregatedfeed.local.AggregatedFeedCacheEntity
import fi.kroon.vadret.aggregatedfeed.local.AggregatedFeedEntity
import fi.kroon.vadret.aggregatedfeed.local.AggregatedFeedLocalDataSource
import fi.kroon.vadret.aggregatedfeed.model.AggregatedFeed
import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.aggregatedfeed.remote.KrisinformationFeedDto
import fi.kroon.vadret.aggregatedfeed.remote.KrisinformationRemoteDataSource
import fi.kroon.vadret.aggregatedfeed.remote.SmhiWarningDto
import fi.kroon.vadret.aggregatedfeed.remote.SmhiWarningRemoteDataSource
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.common.catch
import fi.kroon.vadret.common.either
import fi.kroon.vadret.logging.Logger
import fi.kroon.vadret.logging.LoggerFactory
import io.github.sphrak.either.Either
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

private val logger: Logger = LoggerFactory.getLogger(tag = "fi.kroon.vadret.aggregatedfeed")

private val cacheLifetime: Duration = 5.minutes

private const val COUNTY_SEPARATOR = ","

internal class DefaultAggregatedFeedRepository(
    private val smhiWarningRemoteDataSource: SmhiWarningRemoteDataSource,
    private val krisinformationRemoteDataSource: KrisinformationRemoteDataSource,
    private val aggregatedFeedLocalDataSource: AggregatedFeedLocalDataSource,
    private val clock: Clock
) : AggregatedFeedRepository {

    override suspend fun getAggregatedFeed(
        feedSources: Set<FeedSource>,
        districtIds: Set<Int>,
        isRefresh: Boolean
    ): Either<Failure, List<AggregatedFeed>> =
        either {
            getAggregatedFeedCacheEntity(
                feedSources = feedSources,
                districtIds = districtIds,
                isRefresh = isRefresh
            ).items.mapNotNull { aggregatedFeedEntity: AggregatedFeedEntity -> aggregatedFeedEntity.toAggregatedFeed() }
        }.catch { exception: Exception -> exception.toFailure() }

    private suspend fun getAggregatedFeedCacheEntity(
        feedSources: Set<FeedSource>,
        districtIds: Set<Int>,
        isRefresh: Boolean
    ): AggregatedFeedCacheEntity {
        val now: Instant = clock.now()
        val cachedFeed: AggregatedFeedCacheEntity? = if (isRefresh) {
            null
        } else {
            aggregatedFeedLocalDataSource.getAggregatedFeed()
        }
        val isCurrent: Boolean = cachedFeed != null &&
            cachedFeed.isFor(
                feedSources = feedSources,
                districtIds = districtIds
            ) &&
            cachedFeed.isFresh(now = now)
        return if (cachedFeed != null && isCurrent) {
            cachedFeed
        } else {
            val fetchedFeed = AggregatedFeedCacheEntity(
                fetchedAtEpochMillis = now.toEpochMilliseconds(),
                feedSources = feedSources.toCacheKey(),
                districtIds = districtIds.sorted(),
                items = fetchItems(
                    feedSources = feedSources,
                    districtIds = districtIds
                )
            )
            aggregatedFeedLocalDataSource.setAggregatedFeed(aggregatedFeedCacheEntity = fetchedFeed)
            fetchedFeed
        }
    }

    private suspend fun fetchItems(
        feedSources: Set<FeedSource>,
        districtIds: Set<Int>
    ): List<AggregatedFeedEntity> =
        coroutineScope {
            val smhiWarnings: Deferred<List<AggregatedFeedEntity>> = async {
                if (FeedSource.SMHI in feedSources) {
                    getSmhiWarnings(districtIds = districtIds)
                } else {
                    emptyList()
                }
            }
            val krisinformation: Deferred<List<AggregatedFeedEntity>> = async {
                if (FeedSource.KRISINFORMATION in feedSources) {
                    getKrisinformation(districtIds = districtIds)
                } else {
                    emptyList()
                }
            }
            (smhiWarnings.await() + krisinformation.await())
                .distinctBy { aggregatedFeedEntity: AggregatedFeedEntity -> aggregatedFeedEntity.identifier }
                .sortedByDescending { aggregatedFeedEntity: AggregatedFeedEntity -> aggregatedFeedEntity.publishedAtEpochMillis }
        }

    private suspend fun getSmhiWarnings(districtIds: Set<Int>): List<AggregatedFeedEntity> =
        smhiWarningRemoteDataSource
            .getWarnings()
            .flatMap { smhiWarningDto: SmhiWarningDto -> smhiWarningDto.toAggregatedFeedEntities(districtIds = districtIds) }

    private suspend fun getKrisinformation(districtIds: Set<Int>): List<AggregatedFeedEntity> {
        val counties: String = districtIds
            .sorted()
            .joinToString(separator = COUNTY_SEPARATOR)
        val news: List<KrisinformationFeedDto> = krisinformationRemoteDataSource.getNews(counties = counties)
        return (news + getVmas(counties = counties))
            .mapNotNull { krisinformationFeedDto: KrisinformationFeedDto -> krisinformationFeedDto.toAggregatedFeedEntity() }
    }

    private suspend fun getVmas(counties: String): List<KrisinformationFeedDto> =
        try {
            krisinformationRemoteDataSource.getVmas(counties = counties)
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (exception: Exception) {
            logger.warn { "Skipping VMAs: ${exception.javaClass.simpleName}: ${exception.message}" }
            emptyList()
        }

    private fun AggregatedFeedCacheEntity.isFor(
        feedSources: Set<FeedSource>,
        districtIds: Set<Int>
    ): Boolean = this.feedSources == feedSources.toCacheKey() && this.districtIds == districtIds.sorted()

    private fun AggregatedFeedCacheEntity.isFresh(now: Instant): Boolean {
        val age: Duration = now - Instant.fromEpochMilliseconds(epochMilliseconds = fetchedAtEpochMillis)
        return age >= Duration.ZERO && age < cacheLifetime
    }

    private fun Set<FeedSource>.toCacheKey(): List<String> = map { feedSource: FeedSource -> feedSource.name }.sorted()
}