package fi.kroon.vadret.data.feedsource.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fi.kroon.vadret.data.feedsource.model.FeedSourceEntity

@Dao
interface FeedSourceDao {

    @Query("SELECT count(feed_source.id) FROM feed_source")
    suspend fun count(): Int

    @Query("SELECT * FROM feed_source")
    suspend fun getAll(): List<FeedSourceEntity>

    @Query("SELECT * FROM feed_source WHERE id IN (:entityIds)")
    suspend fun getByIds(entityIds: IntArray): List<FeedSourceEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM feed_source_used_by WHERE used_by = :usedBy LIMIT 1)")
    suspend fun checkExists(usedBy: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FeedSourceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entityList: List<FeedSourceEntity>): List<Long>

    @Delete
    suspend fun delete(entity: FeedSourceEntity)
}