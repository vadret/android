package fi.kroon.vadret.data.districtpreference.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import fi.kroon.vadret.data.district.model.DistrictOptionEntity
import fi.kroon.vadret.data.districtpreference.model.DistrictPreferenceEntity
import io.reactivex.Single

@Dao
interface DistrictPreferenceDao {

    @Query("SELECT count(district_preference.id) FROM district_preference WHERE used_by = :usedBy")
    fun count(usedBy: String): Single<Int>

    @Query(value = "SELECT * FROM district_preference")
    fun getAll(): Single<List<DistrictPreferenceEntity>>

    @Query(value = "SELECT id AS Int FROM district_preference WHERE used_by = :usedBy AND is_enabled = 1")
    fun getAllEnabledIds(usedBy: String): Single<List<Int>>

    @Query(value = "SELECT * FROM district_preference WHERE id IN (:entityIds)")
    fun getByIds(entityIds: IntArray): Single<List<DistrictPreferenceEntity>>

    @Query(value = "SELECT * FROM district_used_by WHERE used_by = :usedBy")
    fun getAllUsedBy(usedBy: String): Single<List<DistrictOptionEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(entity: DistrictPreferenceEntity): Single<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(entityList: List<DistrictPreferenceEntity>): Single<List<Long>>

    @Update
    fun update(entity: DistrictPreferenceEntity): Single<Int>

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun update(entityList: List<DistrictPreferenceEntity>): Single<Int>

    @Delete
    fun delete(entity: DistrictPreferenceEntity): Single<Unit>

    @Query(value = "DELETE FROM district_preference")
    fun deleteAll(): Single<Unit>

    @Query(value = "DELETE FROM district_preference WHERE (is_enabled = 1 AND used_by = :usedBy)")
    fun deleteAllEnabledUsedBy(usedBy: String): Single<Unit>
}