package fi.kroon.vadret.data.feedsourcepreference.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import fi.kroon.vadret.data.feedsource.model.FeedSourceOptionEntity
import fi.kroon.vadret.data.feedsourcepreference.model.FeedSourcePreferenceEntity

@Dao
interface FeedSourcePreferenceDao {

    @Query("SELECT count(feed_source_preference.id) FROM feed_source_preference WHERE used_by = :usedBy")
    suspend fun count(usedBy: String): Int

    @Query(value = "SELECT * FROM feed_source_preference")
    suspend fun getAll(): List<FeedSourcePreferenceEntity>

    @Query(value = "SELECT feed_source_id FROM feed_source_preference WHERE used_by = :usedBy AND is_enabled = 1")
    suspend fun getAllEnabledIds(usedBy: String): List<Int>

    @Query(value = "SELECT * FROM feed_source_preference WHERE id IN (:entityIds)")
    suspend fun getByIds(entityIds: IntArray): List<FeedSourcePreferenceEntity>

    @Query(value = "SELECT * FROM feed_source_used_by WHERE used_by = :usedBy")
    suspend fun getAllUsedBy(usedBy: String): List<FeedSourceOptionEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: FeedSourcePreferenceEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entityList: List<FeedSourcePreferenceEntity>): List<Long>

    @Update
    suspend fun update(entity: FeedSourcePreferenceEntity): Int

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(entityList: List<FeedSourcePreferenceEntity>): Int

    @Delete
    suspend fun delete(entity: FeedSourcePreferenceEntity)

    @Query(value = "DELETE FROM feed_source_preference")
    suspend fun deleteAll()

    @Query(value = "DELETE FROM feed_source_preference WHERE (is_enabled = 1 AND used_by = :usedBy)")
    suspend fun deleteAllEnabledUsedBy(usedBy: String)
}