package fi.kroon.vadret.domain.about

import fi.kroon.vadret.BaseUnitTest
import fi.kroon.vadret.data.aboutinfo.AboutInfoRepository
import fi.kroon.vadret.data.aboutinfo.exception.AboutInfoEntityFailure
import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.domain.AboutInfoTask
import fi.kroon.vadret.utils.extensions.asLeft
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn

class AboutInfoTaskTest : BaseUnitTest() {

    @Mock
    private lateinit var mockAboutInfoRepository: AboutInfoRepository

    private lateinit var testAboutInfoTask: AboutInfoTask

    @Mock
    private lateinit var mockAboutInfoList: List<AboutInfo>

    @Before
    fun setup() {
        testAboutInfoTask = AboutInfoTask(mockAboutInfoRepository)
    }

    @Test
    fun `task returns expected objects`() {
        doReturn(getAboutInfoList())
            .`when`(mockAboutInfoRepository)()

        testAboutInfoTask()
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Right<List<AboutInfo>> && it.b == mockAboutInfoList }
    }

    @Test
    fun `task throws internal error`() {
        doReturn(getAboutInfoListFailure())
            .`when`(mockAboutInfoRepository)()

        testAboutInfoTask()
            .test()
            .assertComplete()
            .assertNoErrors()
            .assertValueAt(0) { it is Either.Left<Failure> && it.a is AboutInfoEntityFailure.NoAboutInfoEntityAvailable }
    }

    @Test
    fun `task throws exception`() {
        doReturn(throwException())
            .`when`(mockAboutInfoRepository)()

        testAboutInfoTask()
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