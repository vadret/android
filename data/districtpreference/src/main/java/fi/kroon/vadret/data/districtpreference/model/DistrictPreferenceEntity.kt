package fi.kroon.vadret.data.districtpreference.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

@Entity(
    tableName = "district_preference",
    foreignKeys = [
        ForeignKey(
            entity = DistrictPreferenceEntity::class,
            parentColumns = [
                "id"
            ],
            childColumns = [
                "district_id"
            ]
        )
    ],
    indices = [
        Index(
            value = ["district_id"]
        )
    ]
)
data class DistrictPreferenceEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int? = null,

    @ColumnInfo(name = "district_id")
    val districtId: Int,

    @ColumnInfo(name = "used_by")
    val usedBy: String,

    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean,

    @ColumnInfo(name = "created_at")
    val createdAt: OffsetDateTime = OffsetDateTime.now()

)