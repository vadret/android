package fi.kroon.vadret.autocomplete.local

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import java.io.BufferedReader
import java.io.InputStream

private const val LOCALITY_COLUMN = "locality"
private const val MUNICIPALITY_COLUMN = "municipality"
private const val COUNTY_COLUMN = "county"
private const val LATITUDE_COLUMN = "latitude"
private const val LONGITUDE_COLUMN = "longitude"

/**
 *  The places are read once, every search after that is in memory.
 *
 *  @param openPlaces opens the bundled CSV of places.
 */
internal class DefaultAutoCompleteLocalDataSource(
    private val openPlaces: () -> InputStream,
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
                val readPlaces: List<AutoCompleteItemEntity> = readPlaces()
                places = readPlaces
                readPlaces
            }
        }

    private fun readPlaces(): List<AutoCompleteItemEntity> =
        openPlaces().bufferedReader().use { reader: BufferedReader ->
            CSVParser(
                reader,
                CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim()
            ).records
                .map { csvRecord: CSVRecord -> csvRecord.toAutoCompleteItemEntity() }
        }

    private fun CSVRecord.toAutoCompleteItemEntity(): AutoCompleteItemEntity =
        AutoCompleteItemEntity(
            locality = get(LOCALITY_COLUMN),
            municipality = get(MUNICIPALITY_COLUMN),
            county = get(COUNTY_COLUMN),
            latitude = get(LATITUDE_COLUMN).toDouble(),
            longitude = get(LONGITUDE_COLUMN).toDouble()
        )
}