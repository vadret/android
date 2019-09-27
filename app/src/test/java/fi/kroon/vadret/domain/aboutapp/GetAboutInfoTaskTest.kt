package fi.kroon.vadret.domain.aboutapp

import fi.kroon.vadret.data.aboutinfo.AboutInfoRepository
import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.util.extension.asRight
import fi.kroon.vadret.util.extension.asSingle
import io.github.sphrak.either.Either
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GetAboutInfoTaskTest {

    private val list: List<AboutInfo> = listOf(
        AboutInfo(
            iconResourceId = 0,
            titleResourceId = 0,
            urlResourceId = 0,
            hintResourceId = 0
        )
    )

    private lateinit var testGetAboutInfoTask: GetAboutInfoTask

    @Mock
    private lateinit var mockAboutInfoRepository: AboutInfoRepository

    @Before
    fun setup() {
        testGetAboutInfoTask =
            GetAboutInfoTask(aboutInfoRepository = mockAboutInfoRepository)
    }

    @Test
    fun `load valid about info list`() {

        Mockito.doReturn(getEither())
            .`when`(mockAboutInfoRepository)
            .invoke()

        testGetAboutInfoTask()
            .test()
            .assertNoErrors()
            .assertComplete()
            .assertValueAt(0) {
                it is Either.Right && it.b == list
            }
    }

    private fun getEither(): Single<Either<Failure, List<AboutInfo>>> =
        list.asRight()
            .asSingle()
}