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
class SetRadarMemoryCacheTaskTest {

    private val errorHandler: ErrorHandler = ErrorHandler()

    private lateinit var testSetRadarMemoryCacheTask: SetRadarMemoryCacheTask

    @Mock
    private lateinit var radar: Radar

    @Mock
    private lateinit var mockRadarCacheDataSource: RadarCacheDataSource

    @Before
    fun setup() {
        testSetRadarMemoryCacheTask = SetRadarMemoryCacheTask(cache = mockRadarCacheDataSource)
    }

    @Test
    fun `set cache successfully and return radar data`() = runTest {

        doReturn(getResultEither())
            .`when`(mockRadarCacheDataSource)
            .updateMemoryCache(radar)

        assertTrue(
            testSetRadarMemoryCacheTask(radar)
                .let {
                    it is Either.Right &&
                        it.b == radar
                }
        )
    }

    @Test
    fun `set cache fails with cache write failure`() = runTest {
        doReturn(errorHandler.getCacheWriteError<Radar>())
            .`when`(mockRadarCacheDataSource)
            .updateMemoryCache(radar)

        assertTrue(
            testSetRadarMemoryCacheTask(radar)
                .let {
                    it is Either.Left &&
                        it.a is Failure.CacheWriteError &&
                        (it.a as Failure.CacheWriteError).message == "error: failed writing to cache"
                }
        )
    }

    private fun getResultEither(): Either<Failure, Radar> =
        radar
            .asRight()
}