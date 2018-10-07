package fi.kroon.vadret.data.sharedpreferences

import android.content.Context
import android.content.SharedPreferences
import fi.kroon.vadret.data.exception.Either
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SharedPreferencesRepositoryTest {

    @Mock
    lateinit var mockSharedPreferences: SharedPreferences

    @Mock
    lateinit var mockEditor: SharedPreferences.Editor

    @Mock
    lateinit var mockUnit: Unit

    @Mock
    lateinit var mockContext: Context

    private lateinit var mockSharedPreferencesRepository: SharedPreferencesRepository

    private val preferenceDefaultValue = null
    private val preferenceValueString: String = "blue"
    private val preferenceValueBoolean: Boolean = false
    private val preferenceKey: String = "color"

    /*doReturn(mockSharedPreferences)
            .`when`(mockContext).getSharedPreferences(anyString(), anyInt())*/
    /*doReturn(mockEditor)
        .`when`(mockEditor)
        .putString(anyString(), anyString())*/

    @Before
    fun setup() {
        mockSharedPreferencesRepository = SharedPreferencesRepository(mockSharedPreferences)
    }

    /**
     *  String set/put
     */
    @Test
    fun `repository returns gettable string value`() {

        doReturn(preferenceValueString)
            .`when`(mockSharedPreferences)
            .getString(preferenceKey, preferenceDefaultValue)

        mockSharedPreferencesRepository
            .getString(preferenceKey)
            .test()
            .assertValueAt(0) { it is Either.Right<String> && it.b == preferenceValueString }
    }

    @Test
    fun `repository saves string value`() {
        /**
         * fail
         */

        doReturn(mockEditor)
            .`when`(mockEditor)
            .putString(anyString(), anyString())
        doReturn(mockEditor)
            .`when`(mockSharedPreferences)
            .edit()

        mockSharedPreferencesRepository
            .putString(preferenceKey, preferenceValueString)
            .test()
            .assertValueAt(0) { it is Either.Right<Unit> && it.b == Unit }
    }

    /**
     *  Boolean set/put
     */
    @Test
    fun `repository returns gettable boolean value`() {

        doReturn(preferenceValueBoolean)
            .`when`(mockSharedPreferences)
            .getBoolean(preferenceKey, false)

        mockSharedPreferencesRepository
            .getBoolean(preferenceKey)
            .test()
            .assertValueAt(0) { it is Either.Right<Boolean> && it.b == preferenceValueBoolean }
    }

    @Test
    fun `repository saves boolean value`() {

        doReturn(mockEditor)
            .`when`(mockEditor)
            .putBoolean(anyString(), anyBoolean())
        doReturn(mockEditor)
            .`when`(mockSharedPreferences)
            .edit()

        mockSharedPreferencesRepository
            .putBoolean(preferenceKey, preferenceValueBoolean)
            .test()
            .assertValueAt(0) { it is Either.Right<Unit> && it.b == Unit }
    }
}