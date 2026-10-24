package fi.kroon.vadret.warningfilter.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query

@Dao
internal interface FeedSourcePreferenceDao {

    @Query(value = "SELECT feed_source, is_enabled FROM feed_source_preference")
    suspend fun getAll(): List<FeedSourcePreferenceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(feedSourcePreferenceEntities: List<FeedSourcePreferenceEntity>)
}