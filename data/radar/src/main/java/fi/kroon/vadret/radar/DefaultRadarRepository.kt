package fi.kroon.vadret.radar

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.common.catch
import fi.kroon.vadret.common.either
import fi.kroon.vadret.radar.extension.toFailure
import fi.kroon.vadret.radar.extension.toRadar
import fi.kroon.vadret.radar.extension.toRadarCacheEntity
import fi.kroon.vadret.radar.local.RadarCacheEntity
import fi.kroon.vadret.radar.local.RadarLocalDataSource
import fi.kroon.vadret.radar.model.Radar
import fi.kroon.vadret.radar.remote.RadarRemoteDataSource
import io.github.sphrak.either.Either
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

/**
 *  SMHI adds an image every five minutes, so a radar
 *  fetched within the last fifteen is still current.
 */
private val cacheLifetime: Duration = 15.minutes

internal class DefaultRadarRepository(
    private val radarRemoteDataSource: RadarRemoteDataSource,
    private val radarLocalDataSource: RadarLocalDataSource,
    private val clock: Clock
) : RadarRepository {

    override suspend fun getRadar(): Either<Failure, Radar> =
        either { getRadarCacheEntity().toRadar() }
            .catch { exception: Exception -> exception.toFailure() }

    private suspend fun getRadarCacheEntity(): RadarCacheEntity {
        val now: Instant = clock.now()
        val today: LocalDate = now.toLocalDateTime(timeZone = TimeZone.UTC).date
        val cachedRadar: RadarCacheEntity? = radarLocalDataSource.getRadar()
        return if (cachedRadar != null && cachedRadar.isCurrent(now = now, today = today)) {
            cachedRadar
        } else {
            val fetchedRadar: RadarCacheEntity = radarRemoteDataSource
                .getRadar(date = today)
                .toRadarCacheEntity(
                    fetchedAt = now,
                    date = today
                )
            radarLocalDataSource.setRadar(radarCacheEntity = fetchedRadar)
            fetchedRadar
        }
    }

    /**
     *  A radar without images is fetched again, the first
     *  image of a day can be minutes late.
     */
    private fun RadarCacheEntity.isCurrent(
        now: Instant,
        today: LocalDate
    ): Boolean {
        val age: Duration = now - Instant.fromEpochMilliseconds(epochMilliseconds = fetchedAtEpochMillis)
        return date == today.toString() &&
            age >= Duration.ZERO &&
            age < cacheLifetime &&
            images.isNotEmpty()
    }
}