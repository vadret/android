package fi.kroon.vadret.data.feedsourcepreference.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import fi.kroon.vadret.data.feedsource.model.FeedSourceEntity
import org.threeten.bp.OffsetDateTime

@Entity(
    tableName = "feed_source_preference",
    foreignKeys = [
        ForeignKey(
            entity = FeedSourceEntity::class,
            parentColumns = [
                "id"
            ],
            childColumns = [
                "feed_source_id"
            ]
        )
    ],
    indices = [
        Index(
            value = ["feed_source_id"]
        )
    ]
)
data class FeedSourcePreferenceEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int? = null,

    @ColumnInfo(name = "feed_source_id")
    val feedSourceId: Int,

    @ColumnInfo(name = "used_by")
    val usedBy: String,

    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean,

    @ColumnInfo(name = "created_at")
    val createdAt: OffsetDateTime = OffsetDateTime.now()

)