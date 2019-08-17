package fi.kroon.vadret.domain.radar

import fi.kroon.vadret.data.exception.ErrorHandler
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
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
    fun `successfully retrieve cached radar object`() {

        doReturn(getResultEither())
            .`when`(mockRadarCacheDataSource)
            .getDiskCache()

        testGetRadarDiskCacheTask()
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) {
                it is Either.Right &&
                    it.b == radar
            }
    }

    @Test
    fun `fail to retrieve cached radar object and return cache read error`() {

        doReturn(errorHandler.getCacheReadError<Radar>())
            .`when`(mockRadarCacheDataSource)
            .getDiskCache()

        testGetRadarDiskCacheTask()
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) {
                it is Either.Left &&
                    it.a is Failure.CacheReadError &&
                    (it.a as Failure.CacheReadError).message == "error: failed reading from cache"
            }
    }

    private fun getResultEither(): Single<Either<Failure, Radar>> =
        radar
            .asRight()
            .asSingle()
}