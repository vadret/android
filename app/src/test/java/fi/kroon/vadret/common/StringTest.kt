package fi.kroon.vadret.common

import fi.kroon.vadret.BaseUnitTest
import fi.kroon.vadret.utils.extensions.parseToLocalDate
import fi.kroon.vadret.utils.extensions.splitByCommaTakeFirst
import fi.kroon.vadret.utils.extensions.splitBySpaceTakeFirst
import fi.kroon.vadret.utils.extensions.splitToList
import fi.kroon.vadret.utils.extensions.toCoordinate
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime

class StringTest : BaseUnitTest() {

    @Test
    fun `empty list is null`() {
        val emptyString = ""
        val emptyList = emptyList<String>()
        val result = emptyString.splitToList()

        assertThat(result).isInstanceOf(List::class.java)
        assertThat(result).isEqualTo(emptyList)
        assertThat(result.size).isEqualTo(0)
    }

    @Test
    fun `concatenated String is split into list of strings`() {
        val listOfString = listOf("a", "b", "c", "d", "e")
        val data = "a, b, c, d, e"
        val result = data.splitToList()

        assertThat(result).isInstanceOf(List::class.java)
        assertThat(result).isEqualTo(listOfString)
        assertThat(result.size).isEqualTo(listOfString.size)
    }

    @Test
    fun `concatenated string by comma is split and first String returned`() {
        val data = "a, b, c, d, e"
        val result = data.splitByCommaTakeFirst()

        assertThat(result).isInstanceOf(String::class.java)
        assertThat(result).isEqualTo("a")
    }

    @Test
    fun `concatenated string by space is split and first String returned`() {
        val data = "a b c d e"
        val result = data.splitBySpaceTakeFirst()

        assertThat(result).isInstanceOf(String::class.java)
        assertThat(result).isEqualTo("a").isNotEqualTo("b")
    }

    @Test
    fun `parse string into localdate`() {
        val localDate = OffsetDateTime.parse("2018-11-22T22:44:50+01:00").toLocalDate()
        val data = "2018-11-22T22:44:50+01:00"
        val result = data.parseToLocalDate()

        assertThat(result).isInstanceOf(LocalDate::class.java)
        assertThat(result).isEqualTo(localDate)
    }

    // STRING => DOUBLE
    @Test
    fun `0,0 String coordinate is stripped and formatted to Double`() {
        val doubleCoordinate = 0.0
        val rawCoordinate = "0.0"
        assertThat(rawCoordinate.toCoordinate()).isEqualTo(doubleCoordinate)
    }

    @Test
    fun `String coordinate is stripped and formatted to Double`() {
        val doubleCoordinate = 0.777778
        val rawCoordinate = "0.777777777777777777777777777777"
        assertThat(rawCoordinate.toCoordinate()).isEqualTo(doubleCoordinate)
    }

    @Test
    fun `another String coordinate is stripped and formatted to Double`() {
        val doubleCoordinate = 1234.777778
        val rawCoordinate = "1234.777777777777777777777777777777"
        assertThat(rawCoordinate.toCoordinate()).isEqualTo(doubleCoordinate)
    }

    @Test
    fun `-0,0 String coordinate is stripped and formatted to Double`() {
        val doubleCoordinate = -0.0
        val rawCoordinate = "-0.0"
        assertThat(rawCoordinate.toCoordinate()).isEqualTo(doubleCoordinate)
    }

    @Test
    fun `negative String coordinate is stripped and formatted to Double`() {
        val doubleCoordinate = -1234.777778
        val rawCoordinate = "-1234.777777777777777777777777777777"
        assertThat(rawCoordinate.toCoordinate()).isEqualTo(doubleCoordinate)
    }

    @Test
    fun `String negative coordinate is stripped and formatted to Double`() {
        val doubleCoordinate = -0.777778
        val rawCoordinate = "-0.777777777777777777777777777777"
        assertThat(rawCoordinate.toCoordinate()).isEqualTo(doubleCoordinate)
    }
}