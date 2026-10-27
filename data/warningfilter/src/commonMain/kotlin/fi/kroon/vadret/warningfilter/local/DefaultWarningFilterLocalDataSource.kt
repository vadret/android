package fi.kroon.vadret.warningfilter.local

import androidx.room3.withWriteTransaction

internal class DefaultWarningFilterLocalDataSource(
    private val warningFilterDatabase: WarningFilterDatabase
) : WarningFilterLocalDataSource {

    private val districtDao: DistrictDao = warningFilterDatabase.districtDao()
    private val feedSourcePreferenceDao: FeedSourcePreferenceDao = warningFilterDatabase.feedSourcePreferenceDao()

    override suspend fun countDistricts(): Int = districtDao.count()

    override suspend fun getDistricts(): List<DistrictEntity> = districtDao.getAll()

    override suspend fun insertDistricts(districtEntities: List<DistrictEntity>) {
        districtDao.insert(districtEntities = districtEntities)
    }

    override suspend fun getFeedSourcePreferences(): List<FeedSourcePreferenceEntity> = feedSourcePreferenceDao.getAll()

    override suspend fun setWarningFilter(
        enabledDistrictIds: List<Int>,
        feedSourcePreferenceEntities: List<FeedSourcePreferenceEntity>
    ) {
        warningFilterDatabase.withWriteTransaction {
            districtDao.setEnabled(enabledDistrictIds = enabledDistrictIds)
            feedSourcePreferenceDao.upsert(feedSourcePreferenceEntities = feedSourcePreferenceEntities)
        }
    }
}