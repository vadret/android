package fi.kroon.vadret.data.districtpreference.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import fi.kroon.vadret.data.district.model.DistrictOptionEntity
import fi.kroon.vadret.data.districtpreference.model.DistrictPreferenceEntity

@Dao
interface DistrictPreferenceDao {

    @Query("SELECT count(district_preference.id) FROM district_preference WHERE used_by = :usedBy")
    suspend fun count(usedBy: String): Int

    @Query(value = "SELECT * FROM district_preference")
    suspend fun getAll(): List<DistrictPreferenceEntity>

    @Query(value = "SELECT district_id FROM district_preference WHERE used_by = :usedBy AND is_enabled = 1")
    suspend fun getAllEnabledIds(usedBy: String): List<Int>

    @Query(value = "SELECT * FROM district_preference WHERE id IN (:entityIds)")
    suspend fun getByIds(entityIds: IntArray): List<DistrictPreferenceEntity>

    @Query(value = "SELECT * FROM district_used_by WHERE used_by = :usedBy")
    suspend fun getAllUsedBy(usedBy: String): List<DistrictOptionEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: DistrictPreferenceEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entityList: List<DistrictPreferenceEntity>): List<Long>

    @Update
    suspend fun update(entity: DistrictPreferenceEntity): Int

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun update(entityList: List<DistrictPreferenceEntity>): Int

    @Delete
    suspend fun delete(entity: DistrictPreferenceEntity)

    @Query(value = "DELETE FROM district_preference")
    suspend fun deleteAll()

    @Query(value = "DELETE FROM district_preference WHERE (is_enabled = 1 AND used_by = :usedBy)")
    suspend fun deleteAllEnabledUsedBy(usedBy: String)
}