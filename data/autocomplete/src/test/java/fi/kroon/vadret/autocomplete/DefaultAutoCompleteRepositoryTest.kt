package fi.kroon.vadret.autocomplete

import fi.kroon.vadret.autocomplete.local.AutoCompleteItemEntity
import fi.kroon.vadret.autocomplete.local.AutoCompleteLocalDataSource
import fi.kroon.vadret.autocomplete.model.AutoCompleteItem
import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.logging.LogLevel
import fi.kroon.vadret.testing.withRecordedLogs
import io.github.sphrak.either.Either
import kotlinx.coroutines.test.runTest
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals

private class FakeAutoCompleteLocalDataSource(
    private val onSearch: (String, Int) -> List<AutoCompleteItemEntity>
) : AutoCompleteLocalDataSource {
    override suspend fun search(
        prefix: String,
        limit: Int
    ): List<AutoCompleteItemEntity> = onSearch(prefix, limit)
}

class DefaultAutoCompleteRepositoryTest {

    @Test
    fun `found places are returned with the prefix and limit asked for`() = runTest {
        val asked = mutableListOf<Pair<String, Int>>()
        val autoCompleteRepository: AutoCompleteRepository = DefaultAutoCompleteRepository(
            autoCompleteLocalDataSource = FakeAutoCompleteLocalDataSource { prefix: String, limit: Int ->
                asked.add(prefix to limit)
                listOf(
                    AutoCompleteItemEntity(
                        locality = "Abisko",
                        municipality = "Kiruna",
                        county = "Norrbotten",
                        latitude = 68.349,
                        longitude = 18.83
                    )
                )
            }
        )

        val result: Either<Failure, List<AutoCompleteItem>> = autoCompleteRepository.search(prefix = "Abi", limit = 3)

        assertEquals<Either<Failure, List<AutoCompleteItem>>>(
            expected = Either.Right(
                b = listOf(
                    AutoCompleteItem(
                        locality = "Abisko",
                        municipality = "Kiruna",
                        county = "Norrbotten",
                        latitude = 68.349,
                        longitude = 18.83
                    )
                )
            ),
            actual = result
        )
        assertEquals(expected = listOf("Abi" to 3), actual = asked)
    }

    @Test
    fun `a list that cannot be read is a file failure`() = runTest {
        val exceptions: List<Exception> = listOf(
            IOException("missing"),
            NumberFormatException("north"),
            IllegalArgumentException("no column"),
            IllegalStateException("closed")
        )

        withRecordedLogs { recordingLogSink ->
            val results: List<Either<Failure, List<AutoCompleteItem>>> = exceptions.map { exception: Exception ->
                DefaultAutoCompleteRepository(
                    autoCompleteLocalDataSource = FakeAutoCompleteLocalDataSource { _, _ -> throw exception }
                ).search(prefix = "A", limit = 1)
            }

            assertEquals(
                expected = List(size = exceptions.size) { Either.Left(a = Failure(errorCode = ErrorCode.FILE_READ_FAILED)) },
                actual = results
            )
            assertEquals(
                expected = List(size = exceptions.size) { LogLevel.WARN },
                actual = recordingLogSink.lines.map { line -> line.level }
            )
        }
    }

    @Test
    fun `anything else is unknown`() = runTest {
        withRecordedLogs { recordingLogSink ->
            val result: Either<Failure, List<AutoCompleteItem>> = DefaultAutoCompleteRepository(
                autoCompleteLocalDataSource = FakeAutoCompleteLocalDataSource { _, _ -> throw UnsupportedOperationException("bug") }
            ).search(prefix = "A", limit = 1)

            assertEquals(expected = Either.Left(a = Failure.unknown()), actual = result)
            assertEquals(expected = LogLevel.ERROR, actual = recordingLogSink.lines.single().level)
        }
    }
}