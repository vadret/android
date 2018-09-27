package fi.kroon.vadret.data.common

import android.content.Context
import androidx.annotation.RawRes
import timber.log.Timber
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import javax.inject.Inject


class RawTextFileReader
@Inject constructor(private val context: Context) {
    @Throws(IOException::class)
    fun readFile(@RawRes fileId: Int): String {
        try {
            val inS = context.resources.openRawResource(fileId)
            val b = ByteArray(inS.available())

            inS.read(b)

            return if (Charset.isSupported(StandardCharsets.UTF_8.name())) {
                b.toString(StandardCharsets.UTF_8)
            } else {
                b.toString(Charset.defaultCharset())
            }
        } catch (e: IOException) {
            Timber.e(e)
            throw e
        }
    }
}