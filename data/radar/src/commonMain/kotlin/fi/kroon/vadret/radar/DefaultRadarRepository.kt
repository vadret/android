package fi.kroon.vadret.radar

import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.common.catch
import fi.kroon.vadret.common.either
import fi.kroon.vadret.radar.extension.toFailure
import fi.kroon.vadret.radar.extension.toRadar
import fi.kroon.vadret.radar.extension.toRadarCacheEntity
import fi.kroon.vadret.radar.extension.toRadarDate
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

private val cacheLifetime: Duration = 15.minutes

internal class DefaultRadarRepository(
    private val radarRemoteDataSource: RadarRemoteDataSource,
    private val radarLocalDataSource: RadarLocalDataSource,
    private val clock: Clock
) : RadarRepository {

    override suspend fun getRadar(): Either<Failure, Radar> = either {
        getRadarCacheEntity(
            date = clock.now().toLocalDateTime(timeZone = TimeZone.UTC).date
        ).toRadar()
    }.catch { exception: Exception -> exception.toFailure() }

    override suspend fun getRadarContaining(
        imageUrl: String
    ): Either<Failure, Radar> = either {
        getRadarCacheEntity(date = imageUrl.toRadarDate()).toRadar()
    }.catch { exception: Exception -> exception.toFailure() }

    private suspend fun getRadarCacheEntity(date: LocalDate): RadarCacheEntity {
        val now: Instant = clock.now()
        val cachedRadar: RadarCacheEntity? = radarLocalDataSource.getRadar()
        return if (cachedRadar != null && cachedRadar.isCurrent(now = now, date = date)) {
            cachedRadar
        } else {
            val fetchedRadar: RadarCacheEntity = radarRemoteDataSource
                .getRadar(date = date)
                .toRadarCacheEntity(
                    fetchedAt = now,
                    date = date
                )
            radarLocalDataSource.setRadar(radarCacheEntity = fetchedRadar)
            fetchedRadar
        }
    }

    private fun RadarCacheEntity.isCurrent(
        now: Instant,
        date: LocalDate
    ): Boolean {
        val age: Duration = now - Instant.fromEpochMilliseconds(epochMilliseconds = fetchedAtEpochMillis)
        return this.date == date.toString() &&
            age >= Duration.ZERO &&
            age < cacheLifetime &&
            images.isNotEmpty()
    }
}