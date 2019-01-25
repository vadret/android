package fi.kroon.vadret.common

import fi.kroon.vadret.BaseUnitTest
import fi.kroon.vadret.data.functional.Either
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class EitherTest : BaseUnitTest() {

    @Test
    fun `Either Right should return correct type`() {
        val result = Either.Right("hackerman")

        assertThat(result).isInstanceOf(Either::class.java)
        assertThat(result.isRight).isEqualTo(true)
        assertThat(result.isLeft).isEqualTo(false)

        result.either(
            {
            },
            { right -> assertThat(right).isInstanceOf(String::class.java)
                assertThat(right).isEqualTo("hackerman")
            }
        )
    }

    @Test
    fun `Either right should return correct type`() {
        val result = Either.Right("hackerman")

        assertThat(result).isInstanceOf(Either::class.java)
        assertThat(result.isRight).isEqualTo(true)
        assertThat(result.isLeft).isEqualTo(false)

        result.either(
            {
            },
            { right -> assertThat(right).isInstanceOf(String::class.java)
                assertThat(right).isEqualTo("hackerman")
            }
        )
    }
}