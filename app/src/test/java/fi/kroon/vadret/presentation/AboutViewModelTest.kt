package fi.kroon.vadret.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import fi.kroon.vadret.data.changelog.exception.ChangelogFailure
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.domain.ChangelogUseCase
import fi.kroon.vadret.presentation.viewmodel.AboutViewModel
import fi.kroon.vadret.util.RxImmediateSchedulerRule
import io.reactivex.Single
import org.junit.Before
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AboutViewModelTest {
    companion object {
        @ClassRule
        @JvmField
        val schedulers = RxImmediateSchedulerRule()
    }

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var mockUseCase: ChangelogUseCase
    @Mock
    lateinit var observer: Observer<String>

    private lateinit var testViewModel: AboutViewModel

    private val testChangelogText: String = "TEST"

    @Before
    fun setup() {
        testViewModel = AboutViewModel(mockUseCase)
    }

    @Test
    fun getLibraries_shouldReturnLibraries() {
        assert(testViewModel.getLibraries().isNotEmpty())
    }

    @Test
    fun getInfoRows_shouldReturnInfoRows() {
        assert(testViewModel.getInfoRows().isNotEmpty())
    }

    @Test
    fun getChangelogText_shouldReturnInitEmpty() {
        doReturn(Single.just(Either.Right(testChangelogText))).`when`(mockUseCase).get()

        testViewModel.getChangelogText().value.isNullOrBlank()
    }

    @Test
    fun getChangelogText_shouldReturnChangelog() {
        doReturn(Single.just(Either.Right(testChangelogText))).`when`(mockUseCase).get()

        testViewModel.getChangelogText().observeForever(observer)
        verify(observer).onChanged(testChangelogText)
    }

    @Test
    fun getChangelogTextFailure_shouldReturnInitEmpty() {
        doReturn(Single.just(Either.Left(ChangelogFailure.FileNotAvailableFailure()))).`when`(mockUseCase).get()

        testViewModel.getChangelogText().value.isNullOrBlank()
    }
}