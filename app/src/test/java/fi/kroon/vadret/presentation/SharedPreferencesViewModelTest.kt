package fi.kroon.vadret.presentation

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.domain.SharedPreferencesUseCase
import fi.kroon.vadret.presentation.viewmodel.SharedPreferencesViewModel
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.doReturn
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SharedPreferencesViewModelTest {

    @Mock
    private lateinit var mockSharedPreferencesUseCase: SharedPreferencesUseCase

    private lateinit var mockSharedPreferencesViewModel: SharedPreferencesViewModel

    private val preferenceKey: String = "color"
    private val preferenceValueString: String = "blue"
    private val preferenceValueBoolean: Boolean = false

    @Before
    fun setup() {
        mockSharedPreferencesViewModel = SharedPreferencesViewModel(mockSharedPreferencesUseCase)
    }

    /**
     *  String set/put
     */
    @Test
    fun `viewmodel returns correct string value`() {
        val result = Either.Right(preferenceValueString) as Either<Failure, String>
        val resultSingle = Single.just(result)

        doReturn(resultSingle)
            .`when`(mockSharedPreferencesUseCase)
            .getString(preferenceKey)

        mockSharedPreferencesViewModel
            .getString(preferenceKey)
            .test()
            .assertResult(result)
    }

    @Test
    fun `viewmodel saves string value`() {

        val result = Either.Right(Unit) as Either<Failure, Unit>
        val resultSingle = Single.just(result)

        doReturn(resultSingle)
            .`when`(mockSharedPreferencesUseCase)
            .putString(preferenceKey, preferenceValueString)

        mockSharedPreferencesViewModel
            .putString(preferenceKey, preferenceValueString)
            .test()
            .assertResult(result)
    }

    /**
     *  Boolean set/put
     */
    @Test
    fun `viewmodel returns correct boolean value`() {
        val result = Either.Right(preferenceValueBoolean) as Either<Failure, Boolean>
        val resultSingle = Single.just(result)

        Mockito.doReturn(resultSingle)
            .`when`(mockSharedPreferencesUseCase)
            .getBoolean(preferenceKey)

        mockSharedPreferencesViewModel
            .getBoolean(preferenceKey)
            .test()
            .assertResult(result)
    }

    @Test
    fun `viewmodel saves boolean value`() {

        val result = Either.Right(Unit) as Either<Failure, Unit>
        val resultSingle = Single.just(result)

        doReturn(resultSingle)
            .`when`(mockSharedPreferencesUseCase)
            .putBoolean(preferenceKey, preferenceValueBoolean)

        mockSharedPreferencesViewModel
            .putBoolean(preferenceKey, preferenceValueBoolean)
            .test()
            .assertResult(result)
    }
}