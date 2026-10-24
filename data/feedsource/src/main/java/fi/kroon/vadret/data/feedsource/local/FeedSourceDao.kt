package fi.kroon.vadret.data.feedsource.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fi.kroon.vadret.data.feedsource.model.FeedSourceEntity
import io.reactivex.Single

@Dao
interface FeedSourceDao {

    @Query("SELECT count(feed_source.id) FROM feed_source")
    fun count(): Single<Int>

    @Query("SELECT * FROM feed_source")
    fun getAll(): Single<List<FeedSourceEntity>>

    @Query("SELECT * FROM feed_source WHERE id IN (:entityIds)")
    fun getByIds(entityIds: IntArray): Single<List<FeedSourceEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM feed_source_used_by WHERE used_by = :usedBy LIMIT 1)")
    fun checkExists(usedBy: String): Single<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: FeedSourceEntity): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entityList: List<FeedSourceEntity>): Single<List<Long>>

    @Delete
    fun delete(entity: FeedSourceEntity): Single<Unit>
}