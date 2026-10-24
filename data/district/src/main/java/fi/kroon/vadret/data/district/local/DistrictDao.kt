package fi.kroon.vadret.data.district.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fi.kroon.vadret.data.district.model.DistrictEntity

@Dao
interface DistrictDao {

    @Query("SELECT count(district.id) FROM district")
    suspend fun count(): Int

    @Query("SELECT * FROM district")
    suspend fun getAll(): List<DistrictEntity>

    @Query("SELECT * FROM district WHERE id IN (:entityIds)")
    suspend fun getByIds(entityIds: IntArray): List<DistrictEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM feed_source_used_by WHERE used_by = :usedBy LIMIT 1)")
    suspend fun checkExists(usedBy: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: DistrictEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entityList: List<DistrictEntity>): List<Long>

    @Delete
    suspend fun delete(entity: DistrictEntity)
}