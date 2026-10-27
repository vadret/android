package fi.kroon.vadret.autocomplete.local

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

private const val LOCALITY_COLUMN = "locality"
private const val MUNICIPALITY_COLUMN = "municipality"
private const val COUNTY_COLUMN = "county"
private const val LATITUDE_COLUMN = "latitude"
private const val LONGITUDE_COLUMN = "longitude"
private const val COLUMN_SEPARATOR = ','
private const val HEADER_ROWS = 1

internal class DefaultAutoCompleteLocalDataSource(
    private val readPlaces: suspend () -> String,
    private val coroutineDispatcher: CoroutineDispatcher
) : AutoCompleteLocalDataSource {

    private val mutex: Mutex = Mutex()
    private var places: List<AutoCompleteItemEntity>? = null

    override suspend fun search(
        prefix: String,
        limit: Int
    ): List<AutoCompleteItemEntity> =
        withContext(context = coroutineDispatcher) {
            getPlaces()
                .asSequence()
                .filter { autoCompleteItemEntity: AutoCompleteItemEntity ->
                    autoCompleteItemEntity.locality.startsWith(
                        prefix = prefix,
                        ignoreCase = true
                    )
                }
                .take(n = limit)
                .toList()
        }

    private suspend fun getPlaces(): List<AutoCompleteItemEntity> =
        mutex.withLock {
            val cachedPlaces: List<AutoCompleteItemEntity>? = places
            if (cachedPlaces != null) {
                cachedPlaces
            } else {
                val readPlaces: List<AutoCompleteItemEntity> = parsePlaces(text = readPlaces())
                places = readPlaces
                readPlaces
            }
        }

    private fun parsePlaces(text: String): List<AutoCompleteItemEntity> {
        val rows: List<List<String>> = text
            .lineSequence()
            .filter { line: String -> line.isNotBlank() }
            .map { line: String -> line.split(COLUMN_SEPARATOR).map { column: String -> column.trim() } }
            .toList()
        val columnIndexes: Map<String, Int> = rows
            .first()
            .withIndex()
            .associate { column: IndexedValue<String> -> column.value.lowercase() to column.index }
        return rows
            .drop(n = HEADER_ROWS)
            .map { row: List<String> ->
                require(value = row.size == columnIndexes.size) { "Expected ${columnIndexes.size} columns in $row" }
                row.toAutoCompleteItemEntity(columnIndexes = columnIndexes)
            }
    }

    private fun List<String>.toAutoCompleteItemEntity(columnIndexes: Map<String, Int>): AutoCompleteItemEntity =
        AutoCompleteItemEntity(
            locality = column(name = LOCALITY_COLUMN, columnIndexes = columnIndexes),
            municipality = column(name = MUNICIPALITY_COLUMN, columnIndexes = columnIndexes),
            county = column(name = COUNTY_COLUMN, columnIndexes = columnIndexes),
            latitude = column(name = LATITUDE_COLUMN, columnIndexes = columnIndexes).toDouble(),
            longitude = column(name = LONGITUDE_COLUMN, columnIndexes = columnIndexes).toDouble()
        )

    private fun List<String>.column(
        name: String,
        columnIndexes: Map<String, Int>
    ): String = get(index = requireNotNull(value = columnIndexes[name]) { "Missing column $name" })
}