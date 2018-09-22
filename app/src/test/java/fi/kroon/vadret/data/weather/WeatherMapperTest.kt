package fi.kroon.vadret.data.weather

import fi.kroon.vadret.data.weather.model.TimeSerie
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class WeatherMapperTest {
    companion object {
        private const val TEST_TIME = "2018-09-22T12:00:00Z"
    }

    private lateinit var testWeatherMapper: WeatherMapper

    @Before
    fun setup() {
        testWeatherMapper = WeatherMapper()
    }

    @Test
    fun givenEmptyList_shouldReturnEmpty() {
        val emptyList = createEmptyList()

        val result = testWeatherMapper.toAnyList(emptyList)

        assert(result.isEmpty())
    }

    @Test
    fun givenNonEmptySingleList_shouldReturnNonEmpty() {
        val nonEmptyList = createNonEmptyList(1)

        val result = testWeatherMapper.toAnyList(nonEmptyList)

        assert(result.size == nonEmptyList.size.inc())
    }

    @Test
    fun givenNonEmptyMultipleList_shouldReturnNonEmpty() {
        val nonEmptyList = createNonEmptyList(5)

        val result = testWeatherMapper.toAnyList(nonEmptyList)

        assert(result.size == nonEmptyList.size.inc())
    }

    private fun createNonEmptyList(size: Int): List<TimeSerie> =
        List(size) {
            TimeSerie(TEST_TIME, listOf())
        }

    private fun createEmptyList() =
        ArrayList<TimeSerie>()
}