package fi.kroon.vadret.warningfilter.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query

@Dao
internal interface DistrictDao {

    @Query(value = "SELECT COUNT(id) FROM district")
    suspend fun count(): Int

    @Query(value = "SELECT id, name, code, is_enabled FROM district ORDER BY name ASC")
    suspend fun getAll(): List<DistrictEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(districtEntities: List<DistrictEntity>)

    @Query(value = "UPDATE district SET is_enabled = id IN (:enabledDistrictIds)")
    suspend fun setEnabled(enabledDistrictIds: List<Int>)
}