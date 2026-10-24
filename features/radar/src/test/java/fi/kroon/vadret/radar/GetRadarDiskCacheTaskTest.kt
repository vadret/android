package fi.kroon.vadret.radar

import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.radar.cache.RadarCacheDataSource
import fi.kroon.vadret.data.radar.model.Radar
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import kotlinx.coroutines.test.runTest
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import kotlin.test.Test
import kotlin.test.assertTrue

class GetRadarDiskCacheTaskTest {

    private val errorHandler: ErrorHandler = ErrorHandler()

    private val mockRadarCacheDataSource: RadarCacheDataSource = mock(RadarCacheDataSource::class.java)

    private val radar: Radar = mock(Radar::class.java)

    private val testGetRadarDiskCacheTask: GetRadarDiskCacheTask = GetRadarDiskCacheTask(cache = mockRadarCacheDataSource)

    @Test
    fun `successfully retrieve cached radar object`() = runTest {

        doReturn(getResultEither())
            .`when`(mockRadarCacheDataSource)
            .getDiskCache()

        assertTrue(
            testGetRadarDiskCacheTask()
                .let {
                    it is Either.Right &&
                        it.b == radar
                }
        )
    }

    @Test
    fun `fail to retrieve cached radar object and return cache read error`() = runTest {

        doReturn(errorHandler.getCacheReadError<Radar>())
            .`when`(mockRadarCacheDataSource)
            .getDiskCache()

        assertTrue(
            testGetRadarDiskCacheTask()
                .let {
                    it is Either.Left &&
                        it.a is Failure.CacheReadError &&
                        (it.a as Failure.CacheReadError).message == "error: failed reading from cache"
                }
        )
    }

    private fun getResultEither(): Either<Failure, Radar> =
        radar
            .asRight()
}