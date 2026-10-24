package fi.kroon.vadret.warningfilter.local

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "district")
internal data class DistrictEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val districtId: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "code")
    val code: String,
    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean
)