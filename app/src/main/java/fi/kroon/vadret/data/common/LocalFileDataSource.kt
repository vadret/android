package fi.kroon.vadret.data.common

import android.content.Context
import androidx.annotation.RawRes
import fi.kroon.vadret.data.common.exception.LocalFileReaderFailure
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import io.reactivex.Single
import timber.log.Timber
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class LocalFileDataSource @Inject constructor(
    private val context: Context
) {

    fun read(@RawRes fileId: Int) =
        Single.fromCallable {

            val inputStream = context.resources.openRawResource(fileId)
            val byteArray = ByteArray(inputStream.available())

            inputStream.read(byteArray)

            Either.right(
                if (Charset.isSupported(StandardCharsets.UTF_8.name())) {
                    byteArray.toString(StandardCharsets.UTF_8)
                } else {
                    byteArray.toString(Charset.defaultCharset())
                }
            )
        }.doOnError {
            Timber.e("$it")
        }.onErrorReturn {
            Either.left(LocalFileReaderFailure.IOFailure())
        }

    fun readList(@RawRes fileId: Int): Single<Either<Failure, List<String>>> =
        Single.fromCallable {
            val inputStream: InputStream = context.resources.openRawResource(fileId)
            Either.right(inputStream.bufferedReader().readLines())
        }.doOnError {
            Timber.e("$it")
        }.onErrorReturn {
            Either.left(LocalFileReaderFailure.IOFailure())
        }
}