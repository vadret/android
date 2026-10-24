package fi.kroon.vadret.radar

import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.data.radar.cache.RadarCacheDataSource
import fi.kroon.vadret.data.radar.model.Radar
import fi.kroon.vadret.util.extension.asRight
import fi.kroon.vadret.util.extension.asSingle
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.MockitoJUnitRunner
import se.fimbla.either.Either

@RunWith(MockitoJUnitRunner::class)
class GetRadarMemoryCacheTaskTest {

    private val errorHandler: ErrorHandler = ErrorHandler()

    private lateinit var testGetRadarMemoryCacheTask: GetRadarMemoryCacheTask

    @Mock
    private lateinit var radar: Radar

    @Mock
    private lateinit var mockRadarCacheDataSource: RadarCacheDataSource

    @Before
    fun setup() {
        testGetRadarMemoryCacheTask = GetRadarMemoryCacheTask(cache = mockRadarCacheDataSource)
    }

    @Test
    fun `set cache successfully and return radar data`() {

        doReturn(getResultEither())
            .`when`(mockRadarCacheDataSource)
            .getMemoryCache()

        testGetRadarMemoryCacheTask()
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) {
                it is Either.Right &&
                    it.value == radar
            }
    }

    @Test
    fun `set cache fails with cache write failure`() {
        doReturn(errorHandler.getCacheWriteError<Radar>())
            .`when`(mockRadarCacheDataSource)
            .getMemoryCache()

        testGetRadarMemoryCacheTask()
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) {
                it is Either.Left &&
                    it.value is Failure.CacheWriteError &&
                    (it.value as Failure.CacheWriteError).message == "error: failed writing to cache"
            }
    }

    private fun getResultEither(): Single<Either<Failure, Radar>> =
        radar
            .asRight()
            .asSingle()
}