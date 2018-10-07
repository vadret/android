package fi.kroon.vadret.domain

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.sharedpreferences.SharedPreferencesRepository
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SharedPreferencesUseCaseTest {

    @Mock
    private lateinit var mockSharedPreferencesRepository: SharedPreferencesRepository

    private lateinit var mockSharedPreferencesUseCase: SharedPreferencesUseCase

    private val preferenceValueString: String = "blue"
    private val preferenceValueBoolean: Boolean = false
    private val preferenceKey: String = "color"

    @Before
    fun setup() {
        mockSharedPreferencesUseCase = SharedPreferencesUseCase(mockSharedPreferencesRepository)
    }

    /**
     *  String set/put
     */
    @Test
    fun `usecase returns correct string value`() {

        val result = Either.Right(preferenceValueString) as Either<Failure, String>
        val resultSingle = Single.just(result)

        doReturn(resultSingle)
            .`when`(mockSharedPreferencesRepository)
            .getString(preferenceKey)

        mockSharedPreferencesUseCase
            .getString(preferenceKey)
            .test()
            .assertResult(result)
    }

    @Test
    fun `usecase saves string value`() {

        val result = Either.Right(Unit) as Either<Failure, Unit>
        val resultSingle = Single.just(result)
        doReturn(resultSingle)
            .`when`(mockSharedPreferencesRepository)
            .putString(preferenceKey, preferenceValueString)

        mockSharedPreferencesUseCase
            .putString(preferenceKey, preferenceValueString)
            .test()
            .assertResult(result)
    }

    /**
     *  Boolean set/put
     */
    @Test
    fun `usecase returns correct boolean value`() {

        val result = Either.Right(preferenceValueBoolean) as Either<Failure, Boolean>
        val resultSingle = Single.just(result)

        doReturn(resultSingle)
            .`when`(mockSharedPreferencesRepository)
            .getBoolean(preferenceKey)

        mockSharedPreferencesUseCase
            .getBoolean(preferenceKey)
            .test()
            .assertResult(result)
    }

    @Test
    fun `usecase saves boolean value`() {

        val result = Either.Right(Unit) as Either<Failure, Unit>
        val resultSingle = Single.just(result)
        doReturn(resultSingle)
            .`when`(mockSharedPreferencesRepository)
            .putBoolean(preferenceKey, preferenceValueBoolean)

        mockSharedPreferencesUseCase
            .putBoolean(preferenceKey, preferenceValueBoolean)
            .test()
            .assertResult(result)
    }
}