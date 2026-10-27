package fi.kroon.vadret.warningfilter.local

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

private fun district(
    districtId: Int,
    name: String,
    isEnabled: Boolean = false
): DistrictEntity =
    DistrictEntity(
        districtId = districtId,
        name = name,
        code = "SWE_COUNTIES",
        isEnabled = isEnabled
    )

class WarningFilterDatabaseTest {

    private val testScope = TestScope(StandardTestDispatcher())
    private val warningFilterDatabase: WarningFilterDatabase = inMemoryWarningFilterDatabase(
        coroutineDispatcher = StandardTestDispatcher(testScope.testScheduler)
    )
    private val districtDao: DistrictDao = warningFilterDatabase.districtDao()
    private val feedSourcePreferenceDao: FeedSourcePreferenceDao = warningFilterDatabase.feedSourcePreferenceDao()
    private val warningFilterLocalDataSource: WarningFilterLocalDataSource = DefaultWarningFilterLocalDataSource(
        warningFilterDatabase = warningFilterDatabase
    )

    @AfterTest
    fun tearDown() {
        warningFilterDatabase.close()
    }

    @Test
    fun `districts are counted and listed by name`() =
        testScope.runTest {
            districtDao.insert(
                districtEntities = listOf(
                    district(districtId = 3, name = "Uppsala län"),
                    district(districtId = 1, name = "Stockholms län", isEnabled = true),
                    district(districtId = 2, name = "Gotlands län")
                )
            )

            assertEquals(expected = 3, actual = districtDao.count())
            assertEquals(
                expected = listOf(
                    district(districtId = 2, name = "Gotlands län"),
                    district(districtId = 1, name = "Stockholms län", isEnabled = true),
                    district(districtId = 3, name = "Uppsala län")
                ),
                actual = districtDao.getAll()
            )
        }

    @Test
    fun `inserting a stored district keeps the stored row`() =
        testScope.runTest {
            districtDao.insert(
                districtEntities = listOf(
                    district(
                        districtId = 1,
                        name = "Stockholms län",
                        isEnabled = true
                    )
                )
            )

            districtDao.insert(
                districtEntities = listOf(
                    district(
                        districtId = 1,
                        name = "Renamed",
                        isEnabled = false
                    )
                )
            )

            assertEquals(
                expected = listOf(district(districtId = 1, name = "Stockholms län", isEnabled = true)),
                actual = districtDao.getAll()
            )
        }

    @Test
    fun `enabling districts turns every other district off`() =
        testScope.runTest {
            districtDao.insert(
                districtEntities = listOf(
                    district(districtId = 1, name = "A", isEnabled = true),
                    district(districtId = 2, name = "B"),
                    district(districtId = 3, name = "C")
                )
            )

            districtDao.setEnabled(enabledDistrictIds = listOf(2, 3))
            val afterTwo: List<Boolean> = districtDao.getAll().map { districtEntity: DistrictEntity -> districtEntity.isEnabled }
            districtDao.setEnabled(enabledDistrictIds = emptyList())
            val afterNone: List<Boolean> = districtDao.getAll().map { districtEntity: DistrictEntity -> districtEntity.isEnabled }

            assertEquals(expected = listOf(false, true, true), actual = afterTwo)
            assertEquals(expected = listOf(false, false, false), actual = afterNone)
        }

    @Test
    fun `feed source preferences are inserted and then replaced`() =
        testScope.runTest {
            feedSourcePreferenceDao.upsert(
                feedSourcePreferenceEntities = listOf(
                    FeedSourcePreferenceEntity(feedSource = "SMHI", isEnabled = true),
                    FeedSourcePreferenceEntity(feedSource = "KRISINFORMATION", isEnabled = true)
                )
            )
            feedSourcePreferenceDao.upsert(
                feedSourcePreferenceEntities = listOf(FeedSourcePreferenceEntity(feedSource = "SMHI", isEnabled = false))
            )

            assertEquals(
                expected = setOf(
                    FeedSourcePreferenceEntity(feedSource = "SMHI", isEnabled = false),
                    FeedSourcePreferenceEntity(feedSource = "KRISINFORMATION", isEnabled = true)
                ),
                actual = feedSourcePreferenceDao.getAll().toSet()
            )
        }

    @Test
    fun `the whole filter is written together`() =
        testScope.runTest {
            warningFilterLocalDataSource.insertDistricts(
                districtEntities = listOf(
                    district(districtId = 1, name = "A", isEnabled = true),
                    district(districtId = 2, name = "B")
                )
            )

            warningFilterLocalDataSource.setWarningFilter(
                enabledDistrictIds = listOf(2),
                feedSourcePreferenceEntities = listOf(FeedSourcePreferenceEntity(feedSource = "SMHI", isEnabled = false))
            )

            assertEquals(
                expected = listOf(false, true),
                actual = warningFilterLocalDataSource.getDistricts().map { districtEntity: DistrictEntity -> districtEntity.isEnabled }
            )
            assertEquals(
                expected = listOf(FeedSourcePreferenceEntity(feedSource = "SMHI", isEnabled = false)),
                actual = warningFilterLocalDataSource.getFeedSourcePreferences()
            )
            assertEquals(expected = 2, actual = warningFilterLocalDataSource.countDistricts())
        }
}