package fi.kroon.vadret.data.autocomplete.local

import android.content.Context
import androidx.annotation.RawRes
import fi.kroon.vadret.data.autocomplete.exception.LocalFileReaderFailure
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.data.failure.Failure
import fi.kroon.vadret.util.extension.asLeft
import fi.kroon.vadret.util.extension.asRight
import io.github.sphrak.either.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVRecord
import timber.log.Timber
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class LocalFileDataSource @Inject constructor(
    private val context: Context
) {

    suspend fun read(@RawRes fileId: Int): Either<Failure, String> =
        readRaw(fileId) { inputStream: InputStream ->
            val byteArray = ByteArray(inputStream.available())

            inputStream.read(byteArray)

            if (Charset.isSupported(StandardCharsets.UTF_8.name())) {
                byteArray.toString(StandardCharsets.UTF_8)
            } else {
                byteArray.toString(Charset.defaultCharset())
            }
        }

    suspend fun readList(@RawRes fileId: Int): Either<Failure, List<String>> =
        readRaw(fileId) { inputStream: InputStream ->
            inputStream.bufferedReader().readLines()
        }

    suspend fun readCsvList(@RawRes fileId: Int): Either<Failure, List<AutoCompleteItem>> =
        readRaw(fileId) { inputStream: InputStream ->
            val csvParser = CSVParser(
                inputStream.bufferedReader(),
                CSVFormat
                    .DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim()
            )
            csvParser.records.map { csvRecord: CSVRecord ->
                AutoCompleteItem(
                    locality = csvRecord.get("locality"),
                    municipality = csvRecord.get("municipality"),
                    county = csvRecord.get("county"),
                    latitude = csvRecord.get("latitude").toDouble(),
                    longitude = csvRecord.get("longitude").toDouble()
                )
            }.toList()
        }

    private suspend fun <T> readRaw(@RawRes fileId: Int, parse: (InputStream) -> T): Either<Failure, T> =
        withContext(Dispatchers.IO) {
            try {
                context
                    .resources
                    .openRawResource(fileId)
                    .use(parse)
                    .asRight()
            } catch (exception: Exception) {
                Timber.e("$exception")
                LocalFileReaderFailure
                    .ReadFailure
                    .asLeft()
            }
        }
}