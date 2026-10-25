package fi.kroon.vadret.autocomplete.local

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import java.io.InputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private const val PLACES = """Locality,Municipality,County,Latitude,Longitude
Abbekås,Skurup,Skåne,55.399274,13.600275
Abisko,Kiruna,Norrbotten,68.349,18.83
Arvidsjaur, Arvidsjaur ,Norrbotten,65.5917,19.18
Borås,Borås,Västra Götaland,57.721,12.94"""

class DefaultAutoCompleteLocalDataSourceTest {

    private val testScope = TestScope(StandardTestDispatcher())
    private var openCount = 0

    private fun dataSource(places: String = PLACES): AutoCompleteLocalDataSource =
        DefaultAutoCompleteLocalDataSource(
            openPlaces = {
                openCount++
                places.byteInputStream() as InputStream
            },
            coroutineDispatcher = StandardTestDispatcher(testScope.testScheduler)
        )

    @Test
    fun `places starting with the prefix are found ignoring case`() =
        testScope.runTest {
            assertEquals(
                expected = listOf("Abbekås", "Abisko"),
                actual = dataSource()
                    .search(prefix = "ab", limit = 10)
                    .map { autoCompleteItemEntity: AutoCompleteItemEntity -> autoCompleteItemEntity.locality }
            )
        }

    @Test
    fun `columns are trimmed and coordinates parsed`() =
        testScope.runTest {
            assertEquals(
                expected = listOf(
                    AutoCompleteItemEntity(
                        locality = "Arvidsjaur",
                        municipality = "Arvidsjaur",
                        county = "Norrbotten",
                        latitude = 65.5917,
                        longitude = 19.18
                    )
                ),
                actual = dataSource().search(prefix = "Arv", limit = 10)
            )
        }

    @Test
    fun `at most limit places are found, in the order of the list`() =
        testScope.runTest {
            assertEquals(
                expected = listOf("Abbekås", "Abisko"),
                actual = dataSource()
                    .search(prefix = "A", limit = 2)
                    .map { autoCompleteItemEntity: AutoCompleteItemEntity -> autoCompleteItemEntity.locality }
            )
        }

    @Test
    fun `the list is read once`() =
        testScope.runTest {
            val autoCompleteLocalDataSource: AutoCompleteLocalDataSource = dataSource()

            autoCompleteLocalDataSource.search(prefix = "A", limit = 10)
            autoCompleteLocalDataSource.search(prefix = "B", limit = 10)

            assertEquals(expected = 1, actual = openCount)
        }

    @Test
    fun `a coordinate that is not a number throws`() =
        testScope.runTest {
            assertFailsWith<NumberFormatException> {
                dataSource(places = "Locality,Municipality,County,Latitude,Longitude\nA,B,C,north,east").search(prefix = "A", limit = 10)
            }
        }
}