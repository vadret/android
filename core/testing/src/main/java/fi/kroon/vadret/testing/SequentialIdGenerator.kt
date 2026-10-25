package fi.kroon.vadret.testing

import fi.kroon.vadret.idgenerator.IdGenerator
import java.util.concurrent.atomic.AtomicInteger

class SequentialIdGenerator(
    private val ids: List<String>
) : IdGenerator {

    private val nextIndex: AtomicInteger = AtomicInteger(0)

    override fun generate(): String = ids[nextIndex.getAndIncrement()]
}