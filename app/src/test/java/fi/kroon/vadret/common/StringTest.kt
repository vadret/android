package fi.kroon.vadret.common

import fi.kroon.vadret.utils.extensions.parseToLocalDate
import fi.kroon.vadret.utils.extensions.splitByCommaTakeFirst
import fi.kroon.vadret.utils.extensions.splitBySpaceTakeFirst
import fi.kroon.vadret.utils.extensions.splitToList
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime

@RunWith(MockitoJUnitRunner::class)
class StringTest {

    @Test
    fun `empty list is null`() {
        val emptyString = ""
        val emptyList = emptyList<String>()
        val result = emptyString.splitToList()

        Assertions.assertThat(result).isInstanceOf(List::class.java)
        Assertions.assertThat(result).isEqualTo(emptyList)
        Assertions.assertThat(result.size).isEqualTo(0)
    }

    @Test
    fun `concatenated String is split into list of strings`() {
        val listOfString = listOf("a", "b", "c", "d", "e")
        val data = "a, b, c, d, e"
        val result = data.splitToList()

        Assertions.assertThat(result).isInstanceOf(List::class.java)
        Assertions.assertThat(result).isEqualTo(listOfString)
        Assertions.assertThat(result.size).isEqualTo(listOfString.size)
    }

    @Test
    fun `concatenated string by comma is split and first String returned`() {
        val data = "a, b, c, d, e"
        val result = data.splitByCommaTakeFirst()

        Assertions.assertThat(result).isInstanceOf(String::class.java)
        Assertions.assertThat(result).isEqualTo("a")
    }

    @Test
    fun `concatenated string by space is split and first String returned`() {
        val data = "a b c d e"
        val result = data.splitBySpaceTakeFirst()

        Assertions.assertThat(result).isInstanceOf(String::class.java)
        Assertions.assertThat(result).isEqualTo("a").isNotEqualTo("b")
    }

    @Test
    fun `parse string into localdate`() {
        val localDate = OffsetDateTime.parse("2018-11-22T22:44:50+01:00").toLocalDate()
        val data = "2018-11-22T22:44:50+01:00"
        val result = data.parseToLocalDate()

        Assertions.assertThat(result).isInstanceOf(LocalDate::class.java)
        Assertions.assertThat(result).isEqualTo(localDate)
    }
}