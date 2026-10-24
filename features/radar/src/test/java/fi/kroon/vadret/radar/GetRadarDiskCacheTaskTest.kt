package fi.kroon.vadret.radar

import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.radar.cache.RadarCacheDataSource
import fi.kroon.vadret.data.radar.model.Radar
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GetRadarDiskCacheTaskTest {

    private val errorHandler: ErrorHandler = ErrorHandler()

    private lateinit var testGetRadarDiskCacheTask: GetRadarDiskCacheTask

    @Mock
    private lateinit var mockRadarCacheDataSource: RadarCacheDataSource

    @Mock
    private lateinit var radar: Radar

    @Before
    fun setup() {
        testGetRadarDiskCacheTask = GetRadarDiskCacheTask(cache = mockRadarCacheDataSource)
    }

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