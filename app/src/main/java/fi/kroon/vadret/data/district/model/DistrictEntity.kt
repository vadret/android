package fi.kroon.vadret.data.district.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "district")
data class DistrictEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "sort_order")
    val sortOrder: String,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "created_at")
    val createdAt: OffsetDateTime = OffsetDateTime.now()

)