package fi.kroon.vadret.data.common

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

abstract class BaseCache {

    companion object {

        inline fun <reified T> deserializeBytes(bytes: ByteArray): T {
            val byteArrayInputStream = ByteArrayInputStream(bytes)

            val objectInputStream = ObjectInputStream(byteArrayInputStream)
            val objects: T = objectInputStream.readObject() as T

            objectInputStream.close()
            byteArrayInputStream.close()

            return objects
        }

        fun serializerObject(`object`: Any): ByteArray {
            val byteArrayOutputStream: ByteArrayOutputStream = ByteArrayOutputStream()

            val objectOutputStream: ObjectOutputStream = ObjectOutputStream(byteArrayOutputStream).apply {
                writeObject(`object`)
                flush()
            }

            val byteArray: ByteArray?
            byteArray = byteArrayOutputStream.toByteArray()
            byteArrayOutputStream.close()

            objectOutputStream.close()

            return byteArray
        }
    }
}