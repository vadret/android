package fi.kroon.vadret.data.about

import fi.kroon.vadret.data.aboutinfo.AboutInfoRepository
import fi.kroon.vadret.data.aboutinfo.exception.AboutInfoEntityFailure
import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import fi.kroon.vadret.data.aboutinfo.local.AboutInfoLocalDataSource
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.BaseUnitTest
import fi.kroon.vadret.utils.extensions.asLeft
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn

class AboutInfoRepositoryTest : BaseUnitTest() {

    @Mock
    private lateinit var mockAboutInfoLocalDataSource: AboutInfoLocalDataSource

    private lateinit var testAboutInfoRepository: AboutInfoRepository

    @Mock
    private lateinit var mockAboutInfoList: List<AboutInfo>

    @Before
    fun setup() {
        testAboutInfoRepository = AboutInfoRepository(mockAboutInfoLocalDataSource)
    }

    @Test
    fun `repository returns expected objects correctly`() {
        doReturn(getAboutInfoList())
            .`when`(mockAboutInfoLocalDataSource)()

        testAboutInfoRepository()
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Right<List<AboutInfo>> && it.b == mockAboutInfoList }
    }

    @Test
    fun `local data source returns no aboutinfo available to repository`() {
        doReturn(getAboutInfoListFailure())
            .`when`(mockAboutInfoLocalDataSource)()

        testAboutInfoRepository()
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is AboutInfoEntityFailure.NoAboutInfoEntityAvailable }
    }

    @Test
    fun `local data source throws exception repository propagate exception`() {
        doReturn(throwException())
            .`when`(mockAboutInfoLocalDataSource)()

        testAboutInfoRepository()
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is AboutInfoEntityFailure.NoAboutInfoEntityAvailable }
    }

    private fun throwException(): Single<Either<Failure, List<AboutInfo>>> =
        Single.error<Either<Failure, List<AboutInfo>>>(Exception("failure"))

    private fun getAboutInfoList(): Single<Either<Failure, List<AboutInfo>>> = Single.just(
        Either.Right(mockAboutInfoList)
    )

    private fun getAboutInfoListFailure(): Single<Either<Failure, List<AboutInfo>>> = Single.just(
        AboutInfoEntityFailure.NoAboutInfoEntityAvailable().asLeft()
    )
}