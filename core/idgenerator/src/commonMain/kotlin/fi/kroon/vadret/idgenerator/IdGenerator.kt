package fi.kroon.vadret.idgenerator

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface IdGenerator {
    fun generate(): String
}

fun IdGenerator(): IdGenerator = DefaultIdGenerator()

internal class DefaultIdGenerator : IdGenerator {

    @OptIn(ExperimentalUuidApi::class)
    override fun generate(): String = Uuid.generateV7().toString()
}