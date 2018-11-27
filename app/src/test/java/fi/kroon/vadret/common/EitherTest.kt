package fi.kroon.vadret.common

import fi.kroon.vadret.BaseUnitTest
import fi.kroon.vadret.data.exception.Either
import org.assertj.core.api.Assertions
import org.junit.Test

class EitherTest : BaseUnitTest() {

    @Test
    fun `Either Right should return correct type`() {
        val result = Either.Right("hackerman")

        Assertions.assertThat(result).isInstanceOf(Either::class.java)
        Assertions.assertThat(result.isRight).isEqualTo(true)
        Assertions.assertThat(result.isLeft).isEqualTo(false)

        result.either(
            {
            },
            { right -> Assertions.assertThat(right).isInstanceOf(String::class.java)
                Assertions.assertThat(right).isEqualTo("hackerman")
            }
        )
    }

    @Test
    fun `Either Left should return correct type`() {
        val result = Either.Left("hackerman failed")

        Assertions.assertThat(result).isInstanceOf(Either::class.java)
        Assertions.assertThat(result.isRight).isEqualTo(false)
        Assertions.assertThat(result.isLeft).isEqualTo(true)

        result.either(
            { left -> Assertions.assertThat(left).isInstanceOf(String::class.java)
                Assertions.assertThat(left).isEqualTo("hackerman failed")
            },
            {
            }
        )
    }
}