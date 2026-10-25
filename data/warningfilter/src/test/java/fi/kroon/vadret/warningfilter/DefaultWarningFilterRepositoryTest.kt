package fi.kroon.vadret.warningfilter

import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.common.ErrorCode
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.logging.LogLevel
import fi.kroon.vadret.testing.withRecordedLogs
import fi.kroon.vadret.warningfilter.local.DefaultWarningFilterLocalDataSource
import fi.kroon.vadret.warningfilter.local.WarningFilterDatabase
import fi.kroon.vadret.warningfilter.local.inMemoryWarningFilterDatabase
import fi.kroon.vadret.warningfilter.model.DistrictCategory
import fi.kroon.vadret.warningfilter.model.DistrictOption
import fi.kroon.vadret.warningfilter.model.FeedSourceOption
import fi.kroon.vadret.warningfilter.model.WarningFilter
import fi.kroon.vadret.warningfilter.remote.DistrictDto
import fi.kroon.vadret.warningfilter.remote.DistrictRemoteDataSource
import io.github.sphrak.either.Either
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import java.net.UnknownHostException
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

private class FakeDistrictRemoteDataSource(
    var districtDtos: List<DistrictDto>
) : DistrictRemoteDataSource {

    var requestCount: Int = 0
    var exception: Exception? = null

    override suspend fun getDistricts(): List<DistrictDto> {
        requestCount++
        val failure: Exception? = exception
        if (failure != null) {
            throw failure
        }
        return districtDtos
    }
}

private val smhiDistricts: List<DistrictDto> = listOf(
    DistrictDto(id = 3, name = "Uppsala län", code = "SWE_COUNTIES"),
    DistrictDto(id = 1, name = "Stockholms län", code = "SWE_COUNTIES"),
    DistrictDto(id = 41, name = "Bottenviken", code = "SWE_SEA_DISTRICTS"),
    DistrictDto(id = 99, name = "Fjällen", code = "SWE_MOUNTAINS")
)

class DefaultWarningFilterRepositoryTest {

    private val testScope = TestScope(StandardTestDispatcher())
    private val warningFilterDatabase: WarningFilterDatabase = inMemoryWarningFilterDatabase(
        coroutineDispatcher = StandardTestDispatcher(testScope.testScheduler)
    )
    private val districtRemoteDataSource = FakeDistrictRemoteDataSource(districtDtos = smhiDistricts)
    private val warningFilterRepository: WarningFilterRepository = DefaultWarningFilterRepository(
        districtRemoteDataSource = districtRemoteDataSource,
        warningFilterLocalDataSource = DefaultWarningFilterLocalDataSource(warningFilterDatabase = warningFilterDatabase)
    )

    @AfterTest
    fun tearDown() {
        warningFilterDatabase.close()
    }

    @Test
    fun `the first filter has Stockholm and every feed source selected`() =
        testScope.runTest {
            assertEquals<Either<Failure, WarningFilter>>(
                expected = Either.Right(
                    b = WarningFilter(
                        districts = listOf(
                            DistrictOption(districtId = 41, name = "Bottenviken", category = DistrictCategory.SEA_DISTRICT, isEnabled = false),
                            DistrictOption(districtId = 99, name = "Fjällen", category = DistrictCategory.OTHER, isEnabled = false),
                            DistrictOption(districtId = 1, name = "Stockholms län", category = DistrictCategory.COUNTY, isEnabled = true),
                            DistrictOption(districtId = 3, name = "Uppsala län", category = DistrictCategory.COUNTY, isEnabled = false)
                        ),
                        feedSources = listOf(
                            FeedSourceOption(feedSource = FeedSource.KRISINFORMATION, isEnabled = true),
                            FeedSourceOption(feedSource = FeedSource.SMHI, isEnabled = true)
                        )
                    )
                ),
                actual = warningFilterRepository.getWarningFilter()
            )
        }

    @Test
    fun `districts are fetched once`() =
        testScope.runTest {
            warningFilterRepository.getWarningFilter()
            warningFilterRepository.getWarningFilter()

            assertEquals(expected = 1, actual = districtRemoteDataSource.requestCount)
        }

    @Test
    fun `a stored filter is read back`() =
        testScope.runTest {
            warningFilterRepository.getWarningFilter()

            warningFilterRepository.setWarningFilter(
                enabledDistrictIds = setOf(3, 41),
                enabledFeedSources = setOf(FeedSource.SMHI)
            )
            val warningFilter: WarningFilter? = (warningFilterRepository.getWarningFilter() as? Either.Right)?.b

            assertEquals(
                expected = setOf(3, 41),
                actual = warningFilter?.districts
                    ?.filter { districtOption: DistrictOption -> districtOption.isEnabled }
                    ?.map { districtOption: DistrictOption -> districtOption.districtId }
                    ?.toSet()
            )
            assertEquals(
                expected = listOf(
                    FeedSourceOption(feedSource = FeedSource.KRISINFORMATION, isEnabled = false),
                    FeedSourceOption(feedSource = FeedSource.SMHI, isEnabled = true)
                ),
                actual = warningFilter?.feedSources
            )
        }

    @Test
    fun `no districts from SMHI is a failure and nothing is stored`() =
        testScope.runTest {
            districtRemoteDataSource.districtDtos = emptyList()

            withRecordedLogs { recordingLogSink ->
                assertEquals(
                    expected = Either.Left(a = Failure(errorCode = ErrorCode.DISTRICTS_NOT_AVAILABLE)),
                    actual = warningFilterRepository.getWarningFilter()
                )
                assertEquals(expected = LogLevel.WARN, actual = recordingLogSink.lines.single().level)
            }
            districtRemoteDataSource.districtDtos = smhiDistricts
            warningFilterRepository.getWarningFilter()
            assertEquals(expected = 2, actual = districtRemoteDataSource.requestCount)
        }

    @Test
    fun `a network failure while fetching districts is reported`() =
        testScope.runTest {
            districtRemoteDataSource.exception = UnknownHostException("opendata-download-warnings.smhi.se")

            withRecordedLogs {
                assertEquals(
                    expected = Either.Left(a = Failure(errorCode = ErrorCode.NETWORK_UNAVAILABLE)),
                    actual = warningFilterRepository.getWarningFilter()
                )
            }
        }
}