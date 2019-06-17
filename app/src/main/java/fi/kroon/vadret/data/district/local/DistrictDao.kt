package fi.kroon.vadret.data.district.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fi.kroon.vadret.data.district.model.DistrictEntity
import io.reactivex.Single

@Dao
interface DistrictDao {

    @Query("SELECT count(district.id) FROM district")
    fun count(): Single<Int>

    @Query("SELECT * FROM district")
    fun getAll(): Single<List<DistrictEntity>>

    @Query("SELECT * FROM district WHERE id IN (:entityIds)")
    fun getByIds(entityIds: IntArray): Single<List<DistrictEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM feed_source_used_by WHERE used_by = :usedBy LIMIT 1)")
    fun checkExists(usedBy: String): Single<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: DistrictEntity): Single<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entityList: List<DistrictEntity>): Single<List<Long>>

    @Delete
    fun delete(entity: DistrictEntity): Single<Unit>
}