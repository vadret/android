package fi.kroon.vadret.warningfilter

import fi.kroon.vadret.aggregatedfeed.model.FeedSource
import fi.kroon.vadret.common.Failure
import fi.kroon.vadret.common.catch
import fi.kroon.vadret.common.either
import fi.kroon.vadret.warningfilter.exception.DistrictsNotAvailableException
import fi.kroon.vadret.warningfilter.extension.toDistrictEntity
import fi.kroon.vadret.warningfilter.extension.toDistrictOption
import fi.kroon.vadret.warningfilter.extension.toFailure
import fi.kroon.vadret.warningfilter.extension.toFeedSourceOptions
import fi.kroon.vadret.warningfilter.extension.toFeedSourcePreferenceEntity
import fi.kroon.vadret.warningfilter.local.DistrictEntity
import fi.kroon.vadret.warningfilter.local.WarningFilterLocalDataSource
import fi.kroon.vadret.warningfilter.model.WarningFilter
import fi.kroon.vadret.warningfilter.remote.DistrictDto
import fi.kroon.vadret.warningfilter.remote.DistrictRemoteDataSource
import io.github.sphrak.either.Either

private const val DEFAULT_DISTRICT_ID = 1

private const val DEFAULT_FEED_SOURCE_IS_ENABLED = true

internal class DefaultWarningFilterRepository(
    private val districtRemoteDataSource: DistrictRemoteDataSource,
    private val warningFilterLocalDataSource: WarningFilterLocalDataSource
) : WarningFilterRepository {

    override suspend fun getWarningFilter(): Either<Failure, WarningFilter> =
        either {
            storeDistrictsOnce()
            WarningFilter(
                districts = warningFilterLocalDataSource
                    .getDistricts()
                    .map { districtEntity: DistrictEntity -> districtEntity.toDistrictOption() },
                feedSources = warningFilterLocalDataSource
                    .getFeedSourcePreferences()
                    .toFeedSourceOptions(defaultIsEnabled = DEFAULT_FEED_SOURCE_IS_ENABLED)
            )
        }.catch { exception: Exception -> exception.toFailure() }

    override suspend fun setWarningFilter(
        enabledDistrictIds: Set<Int>,
        enabledFeedSources: Set<FeedSource>
    ): Either<Failure, Unit> =
        either {
            warningFilterLocalDataSource.setWarningFilter(
                enabledDistrictIds = enabledDistrictIds.sorted(),
                feedSourcePreferenceEntities = FeedSource.entries.map { feedSource: FeedSource ->
                    feedSource.toFeedSourcePreferenceEntity(isEnabled = feedSource in enabledFeedSources)
                }
            )
        }.catch { exception: Exception -> exception.toFailure() }

    private suspend fun storeDistrictsOnce() {
        if (warningFilterLocalDataSource.countDistricts() == 0) {
            val districtDtos: List<DistrictDto> = districtRemoteDataSource.getDistricts()
            if (districtDtos.isEmpty()) {
                throw DistrictsNotAvailableException()
            }
            warningFilterLocalDataSource.insertDistricts(
                districtEntities = districtDtos.map { districtDto: DistrictDto ->
                    districtDto.toDistrictEntity(isEnabled = districtDto.id == DEFAULT_DISTRICT_ID)
                }
            )
        }
    }
}