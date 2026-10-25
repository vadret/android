package fi.kroon.vadret.idgenerator

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val GENERATED_ID_COUNT = 1000
private const val UUID_VERSION_INDEX = 14

class DefaultIdGeneratorTest {

    private val idGenerator: IdGenerator = IdGenerator()

    @Test
    fun `generated ids are unique`() {
        val ids: Set<String> = List(size = GENERATED_ID_COUNT) { idGenerator.generate() }.toSet()

        assertEquals(expected = GENERATED_ID_COUNT, actual = ids.size)
    }

    @Test
    fun `generated ids are version 7 uuids`() {
        val id: String = idGenerator.generate()

        assertTrue(actual = id.matches(regex = Regex(pattern = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")))
        assertEquals(expected = '7', actual = id[UUID_VERSION_INDEX])
    }
}