package fi.kroon.vadret.data.common

import android.content.Context
import androidx.annotation.RawRes
import fi.kroon.vadret.data.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.data.common.exception.LocalFileReaderFailure
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.utils.extensions.asLeft
import io.reactivex.Single
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

    fun read(@RawRes fileId: Int): Single<Either<Failure, String>> =
        Single.fromCallable {

            val inputStream = context.resources.openRawResource(fileId)
            val byteArray = ByteArray(inputStream.available())

            inputStream.read(byteArray)

            Either.Right(
                if (Charset.isSupported(StandardCharsets.UTF_8.name())) {
                    byteArray.toString(StandardCharsets.UTF_8)
                } else {
                    byteArray.toString(Charset.defaultCharset())
                }
            ) as Either<Failure, String>
        }.doOnError {
            Timber.e("$it")
        }.onErrorReturn {
            LocalFileReaderFailure
                .ReadFailure
                .asLeft()
        }

    fun readList(@RawRes fileId: Int): Single<Either<Failure, List<String>>> =
        Single.fromCallable {
            val inputStream: InputStream = context.resources.openRawResource(fileId)
            Either.Right(inputStream.bufferedReader().readLines())
                as Either<Failure, List<String>>
        }.doOnError {
            Timber.e("$it")
        }.onErrorReturn {
            LocalFileReaderFailure
                .ReadFailure
                .asLeft()
        }

    fun readCsvList(@RawRes fileId: Int): Single<Either<Failure, List<AutoCompleteItem>>> =
        Single.fromCallable {
            val inputStream: InputStream = context.resources.openRawResource(fileId)
            val csvParser = CSVParser(
                inputStream.bufferedReader(),
                CSVFormat
                    .DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim()
            )
            Either.Right(csvParser.records.map { csvRecord: CSVRecord ->
                AutoCompleteItem(
                    locality = csvRecord.get("locality"),
                    municipality = csvRecord.get("municipality"),
                    county = csvRecord.get("county"),
                    latitude = csvRecord.get("latitude").toDouble(),
                    longitude = csvRecord.get("longitude").toDouble()
                )
            }.toList()) as Either<Failure, List<AutoCompleteItem>>
        }.doOnError {
            Timber.e("$it")
        }.onErrorReturn {
            LocalFileReaderFailure
                .ReadFailure
                .asLeft()
        }
}