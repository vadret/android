package fi.kroon.vadret.data.feedsourcepreference.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import fi.kroon.vadret.data.feedsource.model.FeedSourceOptionEntity
import fi.kroon.vadret.data.feedsourcepreference.model.FeedSourcePreferenceEntity
import io.reactivex.Single

@Dao
interface FeedSourcePreferenceDao {

    @Query("SELECT count(feed_source_preference.id) FROM feed_source_preference WHERE used_by = :usedBy")
    fun count(usedBy: String): Single<Int>

    @Query(value = "SELECT * FROM feed_source_preference")
    fun getAll(): Single<List<FeedSourcePreferenceEntity>>

    @Query(value = "SELECT id AS Int FROM feed_source_preference WHERE used_by = :usedBy AND is_enabled = 1")
    fun getAllEnabledIds(usedBy: String): Single<List<Int>>

    @Query(value = "SELECT * FROM feed_source_preference WHERE id IN (:entityIds)")
    fun getByIds(entityIds: IntArray): Single<List<FeedSourcePreferenceEntity>>

    @Query(value = "SELECT * FROM feed_source_used_by WHERE used_by = :usedBy")
    fun getAllUsedBy(usedBy: String): Single<List<FeedSourceOptionEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(entity: FeedSourcePreferenceEntity): Single<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(entityList: List<FeedSourcePreferenceEntity>): Single<List<Long>>

    @Update
    fun update(entity: FeedSourcePreferenceEntity): Single<Int>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun update(entityList: List<FeedSourcePreferenceEntity>): Single<Int>

    @Delete
    fun delete(entity: FeedSourcePreferenceEntity): Single<Unit>

    @Query(value = "DELETE FROM feed_source_preference")
    fun deleteAll(): Single<Unit>

    @Query(value = "DELETE FROM feed_source_preference WHERE (is_enabled = 1 AND used_by = :usedBy)")
    fun deleteAllEnabledUsedBy(usedBy: String): Single<Unit>
}