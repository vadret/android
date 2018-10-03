package fi.kroon.vadret.data.common

import android.content.Context
import androidx.annotation.RawRes
import fi.kroon.vadret.data.common.exception.ReaderFailure
import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import timber.log.Timber
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class RawTextFileReader
@Inject constructor(private val context: Context) {
    @Throws(IOException::class)
    fun readFile(@RawRes fileId: Int): Either<Failure, String> {
        try {
            val inS = context.resources.openRawResource(fileId)
            val b = ByteArray(inS.available())

            inS.read(b)

            return Either.Right(
                if (Charset.isSupported(StandardCharsets.UTF_8.name())) {
                    b.toString(StandardCharsets.UTF_8)
                } else {
                    b.toString(Charset.defaultCharset())
                }
            )
        } catch (e: IOException) {
            Timber.e(e)
            return Either.Left(ReaderFailure.IOFailure(e))
        }
    }
}